package com.gbst.complii.orders;

import com.gbst.complii.configuration.ConfigurationKey;
import com.gbst.complii.configuration.ConfigurationService;
import com.gbst.complii.dataMapper.FoCompliiDataMapper;
import com.gbst.complii.dataMapper.DataAccessHelper;
import com.gbst.complii.service.CompliiService;
import com.gbst.complii.utils.CompliiRequestFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("compliiOrderServiceJob")
@ManagedResource(objectName = "FrontOffice:name=CompliiOrderServiceJob")
public class CompliiOrderServiceJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompliiOrderServiceJob.class);
    // Order update timestamp format: "2018-01-22 06:43:33.099723+10"
    private static final String ORDER_UPDATED_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private String limitNumberOfOrdersSentInBulkRequest = "100";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(ORDER_UPDATED_TIMESTAMP_FORMAT);
    private CompliiService compliiService;
    private ConfigurationService configurationService;
    private boolean forceStop; // allow an option to force stop the job
    private String compliiOrderQuery = null;
    private FoCompliiDataMapper foCompliiDataMapper;
    private DataAccessHelper dataAccessHelper;

    @PostConstruct
    public void setupData() {
        limitNumberOfOrdersSentInBulkRequest = (String) configurationService.getValue(ConfigurationKey.COMPLII_LIMIT_OF_NUMBER_OF_ORDER_RECORD_IN_BULK_REQUEST);
        retrieveQuery();
        LOGGER.debug("Complii  - limitNumberOfOrdersSentInBulkRequest = {}, compliiOrderQuery : {}", compliiOrderQuery);
    }

    @Scheduled(fixedDelay = 30000)
    public void scheduleTaskUsingCronExpression() {
        startOrderCompliiService();
    }

    @ManagedOperation
    @Transactional
    public synchronized void startOrderCompliiService() {
        if (!isOrderServiceEnabled() || forceStop) {
            LOGGER.debug("Complii  - Order Service configuration is turned off or manually forced stopped - forceStop={}. So the job won't run", forceStop);
            return;
        }

        // checking the posting strategy
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_ORDER_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;

        LOGGER.info("Complii  - Retrieving orders to be sent");
        String lastOrderSuccessfulPushTimestamp = configurationService.getValue(ConfigurationKey.COMPLII_ORDER_LAST_SUCCESSFUL_PUSH);
        List<CompliiOrder> orders = retrieveOrders(lastOrderSuccessfulPushTimestamp);

        // post orders
        if (orders != null && !orders.isEmpty()) {
            prePostOrder(useBulkPosting, orders, lastOrderSuccessfulPushTimestamp);
        }
    }

    private void prePostOrder(boolean useBulkPosting, List<CompliiOrder> orders, String lastOrderSuccessfulPushTimestamp) {
        LOGGER.debug("Complii  - Check if we need to break up the request to multiple requests due to the limit in complii bulk request.");
        // process in batches of 100 when necessary and discuss how to handle the case where any batch fails. With the whole lot
        // running on the same transaction, we fall back once any order fails to reach Complii which makes it easier
        // breaking up the orders into chunk of 100 as a limit per Complii request as stated in the API
        List<List<CompliiOrder>> parts = new ArrayList<>();
        int n = orders.size();
        int limit = Integer.parseInt(limitNumberOfOrdersSentInBulkRequest);
        for (int i = 0; i < n; i += limit) {
            parts.add(new ArrayList<>(orders.subList(i, Math.min(n, i + limit))));
        }
        for (List<CompliiOrder> part : parts) {
            postOrders(useBulkPosting, part, lastOrderSuccessfulPushTimestamp);
        }
    }


    private void updateLastSuccessfulPushTimestamp(Date sendTime) {
        String time = dateFormat.format(sendTime);
        LOGGER.info("Complii  - Confirm and record date/time of the last successful push of order data {}", time);
        configurationService.setValue(ConfigurationKey.COMPLII_ORDER_LAST_SUCCESSFUL_PUSH, time);
    }

    private void postOrders(boolean useBulkPosting, List<CompliiOrder> orders, String lastOrderSuccessfulPushTimestamp) {
        LOGGER.info("Complii  - Start posting order to Complii");
        CompliiBulkPostTransactionResult bulkPostResult = null;
        OrderPostResult postResult = null;
        try {
            Date sendTime = new Date();
            if (useBulkPosting) {
                bulkPostResult = compliiService.bulkPostOrderTransaction(orders);
                if (bulkPostResult != null && bulkPostResult.getSuccess().booleanValue()) {
                    // order posted successfully. Update date/time of the last successful push
                    if (lastOrderSuccessfulPushTimestamp != null) {
                        updateLastSuccessfulPushTimestamp(sendTime);
                    }
                } else {
                    LOGGER.error("Complii  - Posting this bulk order failed with error {}. So stop the process and roll back. lastOrderSuccessfulPushTimestamp {}", bulkPostResult, lastOrderSuccessfulPushTimestamp);
                    String errorMessage = "Posting bulk order failed. So stop the process and roll back. lastOrderSuccessfulPushTimestamp : " + lastOrderSuccessfulPushTimestamp;
                    throw new Exception(errorMessage);
                }
            } else {
                for (CompliiOrder order : orders) {
                    postResult = compliiService.postOrder(order);
                    // if any of the order posted fail, notify and roll back the entire slot
                    if (postResult == null || (postResult != null && postResult.getSuccess() == false)) {
                        LOGGER.error("Complii  - Posting this order {} failed with error {}. So stop the process and roll back. lastOrderSuccessfulPushTimestamp {}", order.getOrderNumber(), postResult, lastOrderSuccessfulPushTimestamp);
                        String errorMessage = "Posting this order " + order.getOrderNumber() + " failed. So stop the process and roll back. lastOrderSuccessfulPushTimestamp : \" + lastOrderSuccessfulPushTimestamp;";
                        throw new Exception(errorMessage);
                    }
                }
                // order posted successfully. Update date/time of the last successful push
                if (lastOrderSuccessfulPushTimestamp != null) {
                    updateLastSuccessfulPushTimestamp(sendTime);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Complii  - Error in Complii Order Posting job ", e);
        }
    }

    @ManagedOperation
    @Transactional
    public synchronized void stopCompliiOrderService() {
        LOGGER.info("Complii  - Stopping Complii Order Service job. The next order batch will be paused");
        forceStop = true;
    }

    @ManagedOperation
    @Transactional
    public synchronized void resumeCompliiOrderService() {
        LOGGER.info("Complii  - Resuming Order Service job. Forcing the job to read the query again in case it's updated.");
        forceStop = false;
        retrieveQuery();
    }

    private void retrieveQuery() {
        try {
            String compliiOrderQueryFilePath = configurationService.getValue(ConfigurationKey.COMPLII_ORDER_SQL_FILE);
            compliiOrderQuery = new String(Files.readAllBytes(Paths.get(compliiOrderQueryFilePath)));
        } catch (Exception e) {
            LOGGER.error("Complii  - Error in the complii order sql configuration");
        }
    }

    @ManagedOperation
    @Transactional
    public synchronized void baselineOrdersWithComplii() {
        LOGGER.info("Complii  - Start Order baseline process ");
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_ORDER_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;
        List<Map<String, Object>> resultRows = dataAccessHelper.getData(compliiOrderQuery);
        // process in batches of 100 when necessary and discuss how to handle the case where any batch fails. With the whole lot
        // running on the same transaction, we fall back once any order fails to reach Complii which makes it easier
        List<CompliiOrder> orders = mapOrderQueryResponse(resultRows);
        if (orders != null && !orders.isEmpty()) {
            // breaking up the orders into chunk of 100 as a limit per Complii request as stated in the API
            List<List<CompliiOrder>> parts = new ArrayList<>();
            int n = orders.size();
            int limit = Integer.parseInt(limitNumberOfOrdersSentInBulkRequest);
            for (int i = 0; i < n; i += limit) {
                parts.add(new ArrayList<>(orders.subList(i, Math.min(n, i + limit))));
            }
            for (List<CompliiOrder> part : parts) {
                postOrders(useBulkPosting, part, null);
            }
        } else {
            LOGGER.info("Complii  - There is no orders retrieved. Check the database and SQL query");
        }
    }

    @ManagedOperation
    @Transactional
    public synchronized void manualSendOrders(String dateFrom, String dateTo) {
        LOGGER.info("Complii  - Manual Request to send order updated between {} and ", dateFrom, dateTo);
        if (dateTo == null || dateTo.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat(ORDER_UPDATED_TIMESTAMP_FORMAT);
            dateTo = formatter.format(new Date());
        }
        // insert the where clause to Complii Order Query
        String orderQueryFromTo = compliiOrderQuery + " and (o.row_modified >= '" + dateFrom + "'" + "and o.row_modified <= '" + dateTo + "')";
        List<Map<String, Object>> resultRows = dataAccessHelper.getData(orderQueryFromTo);
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_ORDER_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;
        // process in batches of 100 when necessary
        List<CompliiOrder> orders = mapOrderQueryResponse(resultRows);
        if (orders != null && !orders.isEmpty()) {
            List<List<CompliiOrder>> parts = new ArrayList<>();
            int n = orders.size();
            int limit = Integer.parseInt(limitNumberOfOrdersSentInBulkRequest);
            for (int i = 0; i < n; i += limit) {
                parts.add(new ArrayList<>(orders.subList(i, Math.min(n, i + limit))));
            }
            for (List<CompliiOrder> part : parts) {
                String lastOrderSuccessfulPushTimestamp = configurationService.getValue(ConfigurationKey.COMPLII_ORDER_LAST_SUCCESSFUL_PUSH);
                postOrders(useBulkPosting, part, lastOrderSuccessfulPushTimestamp);
            }
        } else {
            LOGGER.info("Complii  - There is no orders retrieved. Check the database and SQL query");
        }
    }


    private List<CompliiOrder> mapOrderQueryResponse(List<Map<String, Object>> resultRows) {
        List<CompliiOrder> orders = new ArrayList<CompliiOrder>();
        for (Map<String, Object> row : resultRows) {
            CompliiOrder order = new CompliiOrder();
            // populate complii specific fields where necessary
            // The actual fo complii mapping can override it if needed
            order.setLicensee((String) configurationService.getValue(ConfigurationKey.COMPLII_APPKEY));
            for (Map.Entry<String, String> entry : foCompliiDataMapper.getOrderMappingMap().entrySet()) {
                String key = entry.getKey(); // complii field
                String value = entry.getValue(); // fo field
                Object queryValue = row.get(value);
                if (hasValue(value) && queryValue != null) {
                    if (key.equalsIgnoreCase(CompliiRequestFields.ORDER_ID)) {
                        order.setOrderId((Integer) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.LICENSEE)) {
                        order.setLicensee((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.DATA_SOURCE)) {
                        order.setDataSource((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ACCOUNT_ID)) {
                        order.setAccountID((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ADVISER_CODE)) {
                        order.setAdviserCode((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ORDER_NUMBER)) {
                        order.setOrderNumber((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.DATE_CREATED)) {
                        order.setDateCreated(convertFromSQLDateToJAVADate((java.sql.Date) queryValue));
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.TIMESTAMP)) {
                        order.setTimestamp(convertFromTimestampToJAVADate((Timestamp) queryValue));
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.BUYSELL)) {
                        order.setBuySell((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.STOCK)) {
                        order.setStock((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.QTY)) {
                        BigDecimal quantityFromQuery = (BigDecimal) queryValue;
                        order.setQty(quantityFromQuery.intValue());
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.PRICE)) {
                        order.setPrice((BigDecimal) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.BROKERAGE)) {
                        order.setBrokerage((BigDecimal) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.BASIS_OF_ADVICE)) {
                        order.setBasisOfAdvice((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ADVICE_GIVEN)) {
                        order.setAdviceGiven((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.OUTSTANDING)) {
                        order.setOutstanding((Boolean) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ACTION_TAKEN)) {
                        order.setActionTaken((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ORDER_TYPE)) {
                        order.setOrderType((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.PRINCIPAL_APPROVED)) {
                        order.setPrincipalApproved((Boolean) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.PRINCIPAL_APPROVED_DATE)) {
                        order.setPrincipalApprovedDate(convertFromSQLDateToJAVADate((java.sql.Date) queryValue));
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.PRINCIPAL_APPROVED_BY)) {
                        order.setPrincipalApprovedBy((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.CREATED_DATE)) {
                        order.setCreatedDate(convertFromSQLDateToJAVADate((java.sql.Date) queryValue));
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.CREATED_BY)) {
                        order.setCreatedBy((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.UPDATED_BY)) {
                        order.setUpdatedBy((Integer) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.OFFER_LETTER_ORDER)) {
                        order.setOfferLetterOrder((Boolean) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ORDER_TAKER)) {
                        order.setOrderTaker((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ORDER_GIVER)) {
                        order.setOrderGiver((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ORDER_TIME_TEXT)) {
                        order.setOrderTimeText((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.DECLARATIONS)) {
                        order.setDeclarations((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.CONTRACT_NOTE_STATUS)) {
                        order.setContractNoteStatus((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.EXTERNAL_CONTRACT_NOTE_ID)) {
                        order.setExternalContractNoteID((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ORIGINAL_ORDER_ID)) {
                        order.setOriginalOrderID((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.TRADE_POSITION_ID)) {
                        order.setTradePositionID((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.BROKERAGE_GST)) {
                        order.setBrokerageGST((BigDecimal) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.CURRENCY)) {
                        order.setCurrency((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.TIMEZONE)) {
                        order.setTimezone((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.BROKERAGE_DESC)) {
                        order.setBrokerageDesc((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.MERGE_STATUS)) {
                        order.setMergeStatus((Integer) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.COMPLII_IMPORTED_DATE)) {
                        order.setCompliiImportedDate(convertFromSQLDateToJAVADate((java.sql.Date) queryValue));
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.INTERNAL_ORDER_NUMBER)) {
                        order.setInternalOrderNumber((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.INTERNAL_DATA)) {
                        order.setInternalData((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.MARKET_ID)) {
                        order.setMarketID((String) queryValue);
                    }
                }
            }
            orders.add(order);
        }
        return orders;
    }

    private List<CompliiOrder> retrieveOrders(String lastOrderSuccessfulPushTimestamp) {
        if (lastOrderSuccessfulPushTimestamp == null || lastOrderSuccessfulPushTimestamp.isEmpty()) {
            LOGGER.error("Complii  - Last successful push of orders is not present. Baseline is required");
            // perhaps first run
            return null;
        } else {
            // and (o.row_modified >= current_timestamp - interval '10 minutes')
            String timeFilter = " and o.row_modified >= " + "'" + lastOrderSuccessfulPushTimestamp + "'";
            String orderQueryFromLastSuccessfulPush = compliiOrderQuery + timeFilter;
            if (configurationService.getValue(ConfigurationKey.COMPLII_TEST_MODE)) {
                // limit the number of records so as to make it easier for testing in case there are too many records returned
                orderQueryFromLastSuccessfulPush += " limit 2";
            }
            orderQueryFromLastSuccessfulPush += ";";
            List<Map<String, Object>> resultRows = dataAccessHelper.getData(orderQueryFromLastSuccessfulPush);
            return mapOrderQueryResponse(resultRows);
        }
    }

    private boolean hasValue(String s) {
        return s != null && !s.isEmpty();
    }

    private Date convertFromSQLDateToJAVADate(java.sql.Date sqlDate) {
        java.util.Date javaDate = null;
        if (sqlDate != null) {
            javaDate = new Date(sqlDate.getTime());
        }
        return javaDate;
    }

    private Date convertFromTimestampToJAVADate(Timestamp timestamp) {
        if (timestamp != null) {
            return new Date(timestamp.getTime());
        }
        return null;
    }

    @ManagedOperation
    @Transactional
    public synchronized ArrayList<CompliiOrder> searchOrder(String licensee, // Identifier for Licensee that the orders relates to. Only authorized licensees can be set
                                                            String orderNumber, // The unique order number for publicly identifying this order with the trade data source
                                                            String stock, // The unique security code for the Order
                                                            String accountID, // The Account Number for the Client linked to the Order
                                                            String adviserCode, // The Adviser's unique code
                                                            String orderType, // The Type of Order - Client or Market
                                                            String mergeStatus, // Used to identify if the Order needs to be merged with another importer before the data is ready. The MergeStatus number is unique
                                                            String compliiImported, // Complii Internal Field - the date the order was imported into Complii feeds
                                                            String dateFrom, // Date to search From - dd/MM/yyyy
                                                            String dateTo, // Date to search To - dd/MM/yyyy
                                                            String marketID // The unique identifier for the Market this Order's security belongs to. Typically should be ASX. AOM is for Option Orders. Use ! in front of Market code to exclude
    ) {
        LOGGER.info("Complii  - Searching Complii Orders");
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            CompliiOrderSearch compliiOrderSearch = new CompliiOrderSearch();
            compliiOrderSearch.setLicensee(licensee);
            compliiOrderSearch.setOrderNumber(orderNumber);
            compliiOrderSearch.setStock(stock);
            compliiOrderSearch.setAdviserCode(adviserCode);
            compliiOrderSearch.setOrderType(orderType);
            compliiOrderSearch.setMergeStatus(Integer.valueOf(mergeStatus));
            compliiOrderSearch.setCompliiImported(Boolean.valueOf(compliiImported));
            compliiOrderSearch.setDateFrom(formatter.parse(dateFrom));
            compliiOrderSearch.setDateTo(formatter.parse(dateTo));
            compliiOrderSearch.setMarketID(marketID);
            List<CompliiOrder> ordersReturned = compliiService.searchOrder(compliiOrderSearch);
            LOGGER.info("Complii  - Complii Orders returned {} orders", ordersReturned.size());
            return new ArrayList<>(ordersReturned);
        } catch (Exception e) {
            LOGGER.error("Complii  - Error encountered when searching Complii Orders: {}", e);
            return null;
        }
    }

    private boolean isOrderServiceEnabled() {
        Boolean enable = configurationService.getValue(ConfigurationKey.COMPLII_ORDER_SERVICE_ENABLED);
        return enable != null && enable;
    }

    @Autowired
    public void setCompliiService(CompliiService compliiService) {
        this.compliiService = compliiService;
    }

    @Autowired
    public void setFoCompliiDataMapper(FoCompliiDataMapper foCompliiDataMapper) {
        this.foCompliiDataMapper = foCompliiDataMapper;
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Autowired
    public void setDataAccessHelper(DataAccessHelper dataAccessHelper) {
        this.dataAccessHelper = dataAccessHelper;
    }

    public boolean isForceStop() {
        return forceStop;
    }

    public void setForceStop(boolean forceStop) {
        this.forceStop = forceStop;
    }
}
