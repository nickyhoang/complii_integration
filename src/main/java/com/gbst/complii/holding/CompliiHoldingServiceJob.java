package com.gbst.complii.holding;


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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("compliiHoldingServiceJob")
@ManagedResource(objectName = "FrontOffice:name=CompliiHoldingServiceJob")
public class CompliiHoldingServiceJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompliiHoldingServiceJob.class);
    // Holding update timestamp format: "2018-01-22 06:43:33.099723+10"
    private static final String HOLDING_UPDATED_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private String limitNumberOfHoldingsSentInBulkRequest = "100";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(HOLDING_UPDATED_TIMESTAMP_FORMAT);
    private CompliiService compliiService;
    private ConfigurationService configurationService;
    private boolean forceStop; // allow an option to force stop the job
    private String compliiHoldingQuery = null;
    private FoCompliiDataMapper foCompliiDataMapper;
    private DataAccessHelper dataAccessHelper;

    @PostConstruct
    public void setData() {
        limitNumberOfHoldingsSentInBulkRequest = (String) configurationService.getValue(ConfigurationKey.COMPLII_LIMIT_OF_NUMBER_OF_HOLDING_RECORD_IN_BULK_REQUEST);
        retrieveQuery();
        LOGGER.debug("Complii - limitNumberOfHoldingsSentInBulkRequest = {}, compliiHoldingQuery : {}", limitNumberOfHoldingsSentInBulkRequest, compliiHoldingQuery);
    }

    @Scheduled(fixedDelayString  = "${complii.holding.service.job.fixed.delay.ms}")
    public void scheduleHoldingTask() {
        startHoldingCompliiService();
    }

    @ManagedOperation
    @Transactional
    public synchronized void startHoldingCompliiService() {
        if (!isHoldingServiceEnabled() || forceStop) {
            LOGGER.debug("Complii  - Holding Service configuration is turned off or manually forced stopped - forceStop={}. So the job won't run", forceStop);
            return;
        }

        // checking the posting strategy
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_HOLDING_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;

        LOGGER.info("Complii - Retrieving holdings to be sent");
        String lastHoldingSuccessfulPushTimestamp = configurationService.getValue(ConfigurationKey.COMPLII_HOLDING_LAST_SUCCESSFUL_PUSH);
        List<CompliiHolding> holdings = retrieveHoldings(lastHoldingSuccessfulPushTimestamp);

        // post holdings
        if (holdings != null && !holdings.isEmpty()) {
            prePostHoldings(useBulkPosting, holdings, lastHoldingSuccessfulPushTimestamp);
        }
    }

    private void prePostHoldings(boolean useBulkPosting, List<CompliiHolding> holdings, String lastHoldingSuccessfulPushTimestamp) {
        LOGGER.debug("Complii  - Check if we need to break up the request to multiple requests due to the limit in complii bulk request.");
        // process in batches of 100 when necessary and discuss how to handle the case where any batch fails. With the whole lot
        // running on the same transaction, we fall back once any holding fails to reach Complii which makes it easier
        // breaking up the holdings into chunk of 100 as a limit per Complii request as stated in the API
        List<List<CompliiHolding>> parts = new ArrayList<>();
        int n = holdings.size();
        int limit = Integer.parseInt(limitNumberOfHoldingsSentInBulkRequest);
        for (int i = 0; i < n; i += limit) {
            parts.add(new ArrayList<>(holdings.subList(i, Math.min(n, i + limit))));
        }
        for (List<CompliiHolding> part : parts) {
            postHoldings(useBulkPosting, part, lastHoldingSuccessfulPushTimestamp);
        }
    }

    @ManagedOperation
    @Transactional
    public synchronized void baselineHoldingsWithComplii() {
        LOGGER.info("Complii - Start Holding baseline process ");
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_HOLDING_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;
        List<Map<String, Object>> resultRows = dataAccessHelper.getData(compliiHoldingQuery);
        // process in batches of 100 when necessary and discuss how to handle the case where any batch fails. With the whole lot
        // running on the same transaction, we fall back once any holding fails to reach Complii which makes it easier
        List<CompliiHolding> holdings = mapHoldingQueryResponse(resultRows);
        if (holdings != null && !holdings.isEmpty()) {
            // breaking up the holdings into chunk of 100 as a limit per Complii request as stated in the API
            List<List<CompliiHolding>> parts = new ArrayList<>();
            int n = holdings.size();
            int limit = Integer.parseInt(limitNumberOfHoldingsSentInBulkRequest);
            for (int i = 0; i < n; i += limit) {
                parts.add(new ArrayList<>(holdings.subList(i, Math.min(n, i + limit))));
            }
            for (List<CompliiHolding> part : parts) {
                postHoldings(useBulkPosting, part, null);
            }
        } else {
            LOGGER.info("Complii - There is no holdings retrieved. Check the database and SQL query");
        }
    }

    private void updateLastSuccessfulPushTimestamp(Date sendTime) {
        String time = dateFormat.format(sendTime);
        LOGGER.info("Complii - Confirm and record date/time of the last successful push of holding data {}", time);
        configurationService.setValue(ConfigurationKey.COMPLII_HOLDING_LAST_SUCCESSFUL_PUSH, time);
    }

    private void postHoldings(boolean useBulkPosting, List<CompliiHolding> holdings, String lastHoldingSuccessfulPushTimestamp) {
        LOGGER.info("Complii - Start posting holding");
        try {
            BulkHoldingPostResult bulkPostResult = null;
            HoldingPostResult postResult = null;
            Date sendTime = new Date();
            if (useBulkPosting) {
                bulkPostResult = compliiService.bulkPostHoldings(holdings);
                if (bulkPostResult != null && bulkPostResult.getSuccess().booleanValue()) {
                    // holding posted successfully
                    updateLastSuccessfulPushTimestamp(sendTime);
                } else {
                    LOGGER.error("Complii - Posting this bulk holding failed with error {}. So stop the process and roll back. lastHoldingSuccessfulPushTimestamp {}", bulkPostResult, lastHoldingSuccessfulPushTimestamp);
                    String errorMessage = "Posting bulk holding failed. So stop the process and roll back. lastHoldingSuccessfulPushTimestamp : " + lastHoldingSuccessfulPushTimestamp;
                    throw new Exception(errorMessage);
                }
            } else {
                for (CompliiHolding holding : holdings) {
                    postResult = compliiService.postHoldings(holding);
                    // if any of the holding posted fail, notify and roll back the entire slot
                    if (postResult == null || (postResult != null && postResult.getSuccess() == false)) {
                        LOGGER.error("Complii - Posting this holding {} failed with error {}. So stop the process and roll back. lastHoldingSuccessfulPushTimestamp {}", holding.getAccountNumber(), postResult, lastHoldingSuccessfulPushTimestamp);
                        String errorMessage = "Posting this holding " + holding.getAccountNumber() + " failed. So stop the process and roll back. lastHoldingSuccessfulPushTimestamp : " + lastHoldingSuccessfulPushTimestamp;
                        throw new Exception(errorMessage);
                    }
                }
                // holding posted successfully
                updateLastSuccessfulPushTimestamp(sendTime);
            }

        } catch (Exception e) {
            LOGGER.error("Complii - Error in Holding Posting job ", e);
        }
    }


    @ManagedOperation
    @Transactional
    public synchronized void stopCompliiHoldingService() {
        LOGGER.info("Complii - Stopping Holding Service job");
        forceStop = true;
    }

    @ManagedOperation
    @Transactional
    public synchronized void manualSendHoldings(String dateFrom, String dateTo) {
        LOGGER.info("Complii - Manual Request ot send holdings updated between {} and ", dateFrom, dateTo);
        if (dateTo == null || dateTo.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            dateTo = formatter.format(new Date());
        }
        String holdingQueryFromTo = compliiHoldingQuery + " and (row_modified >= '" + dateFrom + "'" + "and row_modified <= '" + dateTo + "')";
        List<Map<String, Object>> resultRows = dataAccessHelper.getData(holdingQueryFromTo);
        List<CompliiHolding> holdings = mapHoldingQueryResponse(resultRows);
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_HOLDING_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;
        if (holdings != null && !holdings.isEmpty()) {
            List<List<CompliiHolding>> parts = new ArrayList<>();
            int n = holdings.size();
            int limit = Integer.parseInt(limitNumberOfHoldingsSentInBulkRequest);
            for (int i = 0; i < n; i += limit) {
                parts.add(new ArrayList<>(holdings.subList(i, Math.min(n, i + limit))));
            }
            for (List<CompliiHolding> part : parts) {
                String lastHoldingSuccessfulPushTimestamp = configurationService.getValue(ConfigurationKey.COMPLII_HOLDING_LAST_SUCCESSFUL_PUSH);
                postHoldings(useBulkPosting, part, lastHoldingSuccessfulPushTimestamp);
            }
        } else {
            LOGGER.info("Complii - There is no holdings retrieved. Check the database and SQL query");
        }
    }

    @ManagedOperation
    @Transactional
    public synchronized void resumeCompliiHoldingService() {
        LOGGER.info("Complii  - Resuming Holding Service job. Forcing the job to read the query again in case it's updated.");
        forceStop = false;
        retrieveQuery();
    }

    private void retrieveQuery() {
        String compliiHoldingQueryFilePath = configurationService.getValue(ConfigurationKey.COMPLII_HOLDING_SQL_FILE);
        try {
            compliiHoldingQuery = new String(Files.readAllBytes(Paths.get(compliiHoldingQueryFilePath)));
        } catch (Exception e) {
            LOGGER.error("Complii - Error in the holding sql configuration");
        }
    }


    @ManagedOperation
    @Transactional
    public synchronized ArrayList<CompliiHolding> searchHolding(String licensee,
                                                                String accountNumber,
                                                                String security,
                                                                String market,
                                                                String dateFrom,
                                                                String dateTo,
                                                                Boolean showMostRecentHoldingsOnly

    ) {
        LOGGER.info("Complii - Searching Holding");
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            CompliiHoldingSearch compliiHoldingSearch = new CompliiHoldingSearch();
            compliiHoldingSearch.setLicensee(licensee);
            compliiHoldingSearch.setAccountNumber(accountNumber);
            compliiHoldingSearch.setSecurity(security);
            compliiHoldingSearch.setMarket(market);
            compliiHoldingSearch.setDateFrom(formatter.parse(dateFrom));
            compliiHoldingSearch.setDateTo(formatter.parse(dateTo));
            compliiHoldingSearch.setShowMostRecentHoldingsOnly(showMostRecentHoldingsOnly);
            List<CompliiHolding> holdingsReturned = compliiService.searchHoldings(compliiHoldingSearch);
            LOGGER.info("Complii - Holdings returned {} holdings", holdingsReturned.size());
            return new ArrayList<>(holdingsReturned);
        } catch (Exception e) {
            LOGGER.error("Complii - Error encountered when searching Holdings: {}", e);
            return null;
        }
    }

    private List<CompliiHolding> retrieveHoldings(String lastHoldingSuccessfulPushTimestamp) {
        if (lastHoldingSuccessfulPushTimestamp == null || lastHoldingSuccessfulPushTimestamp.isEmpty()) {
            LOGGER.error("Complii - Last successful push of holdings is not present. Baseline is required");
            // perhaps first run
            return null;
        } else {
            // remember to remove the order by clause in the query if there is. We do not need to sort the data because there's no display involved here.
            String timeFilter = " and row_modified >= " + "'" + lastHoldingSuccessfulPushTimestamp + "'";
            String holdingQueryFromLastSuccessfulPush = compliiHoldingQuery + timeFilter;
            if (configurationService.getValue(ConfigurationKey.COMPLII_TEST_MODE)) {
                // limit the number of records so as to make it easier for testing in case there are too many records returned
                holdingQueryFromLastSuccessfulPush += " limit 2";
            }
            holdingQueryFromLastSuccessfulPush += ";";
            List<Map<String, Object>> resultRows = dataAccessHelper.getData(holdingQueryFromLastSuccessfulPush);
            return mapHoldingQueryResponse(resultRows);
        }

    }

    private List<CompliiHolding> mapHoldingQueryResponse(List<Map<String, Object>> resultRows) {
        List<CompliiHolding> holdings = new ArrayList<CompliiHolding>();
        for (Map<String, Object> row : resultRows) {
            CompliiHolding holding = new CompliiHolding();
            // populate complii specific fields where necessary
            // The actual fo complii mapping can override it if needed
            holding.setLicensee((String) configurationService.getValue(ConfigurationKey.COMPLII_APPKEY));
            for (Map.Entry<String, String> entry : foCompliiDataMapper.getHoldingMappingMap().entrySet()) {
                String key = entry.getKey(); // complii field
                String value = entry.getValue(); // fo field
                Object queryValue = row.get(value);
                if (hasValue(value) && queryValue != null) {
                    if (key.equalsIgnoreCase(CompliiRequestFields.HOLDINGS_ID)) {
                        holding.setHoldingsID((Integer) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.LICENSEE)) {
                        holding.setLicensee((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ACCOUNT_NUMBER)) {
                        holding.setAccountNumber((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.SECURITY)) {
                        holding.setSecurity((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.MARKET)) {
                        holding.setMarket((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.HOLDINGS_DATE)) {
                        holding.setHoldingsDate(convertFromSQLDateToJAVADate((java.sql.Date) queryValue));
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.SPONSORED_VOLUME)) {
                        BigDecimal quantityFromQuery = (BigDecimal) queryValue;
                        holding.setSponsoredVolume(quantityFromQuery.intValue());
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.UNSPONSORED_VOLUME)) {
                        BigDecimal quantityFromQuery = (BigDecimal) queryValue;
                        holding.setUnsponsoredVolume(quantityFromQuery.intValue());
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.PRICE_ON_DATE)) {
                        holding.setPriceOnDate((BigDecimal) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.MARKET_VALUE_ON_DATE)) {
                        holding.setMarketValueOnDate((BigDecimal) queryValue);
                    }
                }
            }
            holdings.add(holding);
        }
        return holdings;
    }

    private boolean hasValue(String s) {
        return s != null && !s.isEmpty();
    }

    private Date convertFromSQLDateToJAVADate(java.sql.Date sqlDate) {
        Date javaDate = null;
        if (sqlDate != null) {
            javaDate = new Date(sqlDate.getTime());
        }
        return javaDate;
    }

    @Autowired
    public void setCompliiService(CompliiService compliiService) {
        this.compliiService = compliiService;
    }

    public boolean isForceStop() {
        return forceStop;
    }

    public void setForceStop(boolean forceStop) {
        this.forceStop = forceStop;
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

    private boolean isHoldingServiceEnabled() {
        Boolean enable = configurationService.getValue(ConfigurationKey.COMPLII_HOLDING_SERVICE_ENABLED);
        return enable != null && enable;
    }
}
