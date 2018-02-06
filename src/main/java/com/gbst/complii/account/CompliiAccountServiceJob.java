package com.gbst.complii.account;


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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("compliiAccountServiceJob")
@ManagedResource(objectName = "FrontOffice:name=CompliiAccountServiceJob")
public class CompliiAccountServiceJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompliiAccountServiceJob.class);
    // Account update timestamp format: "2018-01-22 06:43:33.099723+10"
    private static final String ACCOUNT_UPDATED_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private String limitNumberOfAccountsSentInBulkRequest = "100";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(ACCOUNT_UPDATED_TIMESTAMP_FORMAT);
    private CompliiService compliiService;
    private ConfigurationService configurationService;
    private boolean forceStop; // allow an option to force stop the job
    private String compliiAccountQuery = null;
    private FoCompliiDataMapper foCompliiDataMapper;
    private DataAccessHelper dataAccessHelper;

    @PostConstruct
    public void setupData() {
        limitNumberOfAccountsSentInBulkRequest = (String) configurationService.getValue(ConfigurationKey.COMPLII_LIMIT_OF_NUMBER_OF_ACCOUNT_RECORD_IN_BULK_REQUEST);
        retrieveQuery();
        LOGGER.debug("Complii - limitNumberOfAccountsSentInBulkRequest = {}, compliiAccountQuery : {}", compliiAccountQuery);
    }

    @Scheduled(fixedDelayString  = "${complii.account.service.job.fixed.delay.ms}")
    public void scheduleAccountTask() {
        startAccountCompliiService();
    }

    @ManagedOperation
    @Transactional
    public synchronized void startAccountCompliiService() {
        if (!isAccountServiceEnabled() || forceStop) {
            LOGGER.debug("Complii  - Account Service configuration is turned off or manually forced stopped - forceStop={}. So the job won't run", forceStop);
            return;
        }

        // checking the posting strategy
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_ACCOUNT_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;

        LOGGER.info("Complii - Retrieving accounts to be sent");
        String lastAccountSuccessfulPushTimestamp = configurationService.getValue(ConfigurationKey.COMPLII_ACCOUNT_LAST_SUCCESSFUL_PUSH);
        List<CompliiAccount> accounts = retrieveAccounts(lastAccountSuccessfulPushTimestamp);

        // post accounts
        if (accounts != null && !accounts.isEmpty()) {
            prePostAccounts(useBulkPosting, accounts, lastAccountSuccessfulPushTimestamp);
        }
    }

    private void prePostAccounts(boolean useBulkPosting, List<CompliiAccount> accounts, String lastAccountSuccessfulPushTimestamp) {
        LOGGER.debug("Complii  - Check if we need to break up the request to multiple requests due to the limit in complii bulk request.");
        // process in batches of 100 when necessary and discuss how to handle the case where any batch fails. With the whole lot
        // running on the same transaction, we fall back once any accounts fails to reach Complii which makes it easier
        // breaking up the accounts into chunk of 100 as a limit per Complii request as stated in the API
        List<List<CompliiAccount>> parts = new ArrayList<>();
        int n = accounts.size();
        int limit = Integer.parseInt(limitNumberOfAccountsSentInBulkRequest);
        for (int i = 0; i < n; i += limit) {
            parts.add(new ArrayList<>(accounts.subList(i, Math.min(n, i + limit))));
        }
        for (List<CompliiAccount> part : parts) {
            postAccounts(useBulkPosting, part, lastAccountSuccessfulPushTimestamp);
        }
    }

    @ManagedOperation
    @Transactional
    public synchronized void baselineAccountsWithComplii() {
        LOGGER.info("Complii - Start Account baseline process ");
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_ACCOUNT_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;
        List<Map<String, Object>> resultRows = dataAccessHelper.getData(compliiAccountQuery);
        // process in batches of 100 when necessary and discuss how to handle the case where any batch fails. With the whole lot
        // running on the same transaction, we fall back once any account fails to reach Complii which makes it easier
        List<CompliiAccount> accounts = mapAccountQueryResponse(resultRows);
        if (accounts != null && !accounts.isEmpty()) {
            // breaking up the accounts into chunk of 100 as a limit per Complii request as stated in the API
            List<List<CompliiAccount>> parts = new ArrayList<>();
            int n = accounts.size();
            int limit = Integer.parseInt(limitNumberOfAccountsSentInBulkRequest);
            for (int i = 0; i < n; i += limit) {
                parts.add(new ArrayList<>(accounts.subList(i, Math.min(n, i + limit))));
            }
            for (List<CompliiAccount> part : parts) {
                postAccounts(useBulkPosting, part, null);
            }
        } else {
            LOGGER.info("Complii - There is no accounts retrieved. Check the database and SQL query");
        }
    }

    @ManagedOperation
    @Transactional
    public synchronized void resumeCompliiAccountService() {
        LOGGER.info("Complii  - Resuming Account Service job. Forcing the job to read the query again in case it's updated.");
        forceStop = false;
        retrieveQuery();
    }

    private void retrieveQuery() {
        String compliiAccountQueryFilePath = configurationService.getValue(ConfigurationKey.COMPLII_ACCOUNT_SQL_FILE);
        try {
            compliiAccountQuery = new String(Files.readAllBytes(Paths.get(compliiAccountQueryFilePath)));
        } catch (Exception e) {
            LOGGER.error("Complii - Error in the account sql configuration");
        }
    }

    private void updateLastSuccessfulPushTimestamp(Date sendTime) {
        String time = dateFormat.format(sendTime);
        LOGGER.info("Complii - Confirm and record date/time of the last successful push of account data {}", time);
        configurationService.setValue(ConfigurationKey.COMPLII_ACCOUNT_LAST_SUCCESSFUL_PUSH, time);
    }

    private void postAccounts(boolean useBulkPosting, List<CompliiAccount> accounts, String lastAccountSuccessfulPushTimestamp) {
        LOGGER.info("Complii - Start posting account");
        try {
            BulkAccountPostResult bulkPostResult = null;
            AccountPostResult postResult = null;
            Date sendTime = new Date();
            if (useBulkPosting) {
                bulkPostResult = compliiService.bulkPostAccount(accounts);
                if (bulkPostResult != null && bulkPostResult.getSuccess().booleanValue()) {
                    // account posted successfully
                    updateLastSuccessfulPushTimestamp(sendTime);
                } else {
                    LOGGER.error("Complii - Posting this bulk account failed with error {}. So stop the process and roll back. lastAccountSuccessfulPushTimestamp {}", bulkPostResult, lastAccountSuccessfulPushTimestamp);
                    String errorMessage = "Posting bulk account failed. So stop the process and roll back. lastAccountSuccessfulPushTimestamp : " + lastAccountSuccessfulPushTimestamp;
                    throw new Exception(errorMessage);
                }
            } else {
                for (CompliiAccount account : accounts) {
                    postResult = compliiService.postAccount(account);
                    // if any of the account posted fail, notify and roll back the entire slot
                    if (postResult == null || (postResult != null && postResult.getSuccess() == false)) {
                        LOGGER.error("Complii - Posting this account {} failed with error {}. So stop the process and roll back. lastAccountSuccessfulPushTimestamp {}", account.getAccountNumber(), postResult, lastAccountSuccessfulPushTimestamp);
                        String errorMessage = "Posting this account " + account.getAccountNumber() + " failed. So stop the process and roll back. lastAccountSuccessfulPushTimestamp : " + lastAccountSuccessfulPushTimestamp;
                        throw new Exception(errorMessage);
                    }
                }
                // account posted successfully
                updateLastSuccessfulPushTimestamp(sendTime);
            }

        } catch (Exception e) {
            LOGGER.error("Complii - Error in Account Posting job ", e);
        }

    }

    @ManagedOperation
    @Transactional
    public synchronized void manualSendAccounts(String dateFrom, String dateTo) {
        LOGGER.info("Complii - Manual Request ot send account updated between {} and ", dateFrom, dateTo);
        if (dateTo == null || dateTo.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            dateTo = formatter.format(new Date());
        }
        // insert the where clause to Complii Account Query
        String accountQueryFromTo = compliiAccountQuery + " and (c.row_modified >= '" + dateFrom + "'" + "and c.row_modified <= '" + dateTo + "')";
        List<Map<String, Object>> resultRows = dataAccessHelper.getData(accountQueryFromTo);
        List<CompliiAccount> accounts = mapAccountQueryResponse(resultRows);
        String postingStrategyConfigured = configurationService.getValue(ConfigurationKey.COMPLII_ACCOUNT_POST_STRATEGY);
        boolean useBulkPosting = postingStrategyConfigured.equalsIgnoreCase("BULK") ? true : false;
        if (accounts != null && !accounts.isEmpty()) {
            List<List<CompliiAccount>> parts = new ArrayList<>();
            int n = accounts.size();
            int limit = Integer.parseInt(limitNumberOfAccountsSentInBulkRequest);
            for (int i = 0; i < n; i += limit) {
                parts.add(new ArrayList<>(accounts.subList(i, Math.min(n, i + limit))));
            }
            for (List<CompliiAccount> part : parts) {
                String lastAccountSuccessfulPushTimestamp = configurationService.getValue(ConfigurationKey.COMPLII_ACCOUNT_LAST_SUCCESSFUL_PUSH);
                postAccounts(useBulkPosting, part, lastAccountSuccessfulPushTimestamp);
            }
        } else {
            LOGGER.info("Complii - There is no accounts retrieved. Check the database and SQL query");
        }
    }

    @ManagedOperation
    @Transactional
    public synchronized void stopCompliiAccountService() {
        LOGGER.info("Complii - Stopping Complii Account Service job");
        forceStop = true;
    }

    @ManagedOperation
    @Transactional
    public synchronized ArrayList<CompliiAccount> searchAccount(String licensee,
                                                                String accountNumber,
                                                                String dateFrom,
                                                                String dateTo,
                                                                String branchCode,
                                                                String adviserCode,
                                                                String givenName,
                                                                String surname,
                                                                String email,
                                                                String phoneHome,
                                                                String phoneWork,
                                                                String mobile,
                                                                String hin,
                                                                String rIsoCountry,
                                                                String clientClass,
                                                                String clientType

    ) {
        LOGGER.info("Complii - Searching Complii Account");
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            CompliiAccountSearch compliiAccountSearch = new CompliiAccountSearch();
            compliiAccountSearch.setLicensee(licensee);
            compliiAccountSearch.setAccountNumber(accountNumber);
            compliiAccountSearch.setDateFrom(formatter.parse(dateFrom));
            compliiAccountSearch.setDateTo(formatter.parse(dateTo));
            compliiAccountSearch.setBranchCode(branchCode);
            compliiAccountSearch.setAdviserCode(adviserCode);
            compliiAccountSearch.setGivenName(givenName);
            compliiAccountSearch.setSurname(surname);
            compliiAccountSearch.setEmail(email);
            compliiAccountSearch.setPhoneHome(phoneHome);
            compliiAccountSearch.setPhoneWork(phoneWork);
            compliiAccountSearch.setMobile(mobile);
            compliiAccountSearch.setHin(hin);
            compliiAccountSearch.setrIsoCountry(rIsoCountry);
            compliiAccountSearch.setClientClass(clientClass);
            compliiAccountSearch.setClientType(clientType);
            List<CompliiAccount> accountsReturned = compliiService.searchAccounts(compliiAccountSearch);
            LOGGER.info("Complii - Account returned {} accounts", accountsReturned.size());
            return new ArrayList<>(accountsReturned);
        } catch (Exception e) {
            LOGGER.error("Complii - Error encountered when searching Account: {}", e);
            return null;
        }
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

    private List<CompliiAccount> mapAccountQueryResponse(List<Map<String, Object>> resultRows) {
        List<CompliiAccount> accounts = new ArrayList<CompliiAccount>();
        for (Map<String, Object> row : resultRows) {
            CompliiAccount account = new CompliiAccount();
            // populate complii specific fields where necessary
            // The actual fo complii mapping can override it if needed
            account.setLicensee((String) configurationService.getValue(ConfigurationKey.COMPLII_APPKEY));
            for (Map.Entry<String, String> entry : foCompliiDataMapper.getAccountMappingMap().entrySet()) {
                String key = entry.getKey(); // complii field
                String value = entry.getValue(); // fo field
                Object queryValue = row.get(value);
                if (hasValue(value) && queryValue != null) {
                    if (key.equalsIgnoreCase(CompliiRequestFields.ACCOUNT_ID)) {
                        account.setAccountID(Integer.parseInt((String) queryValue));
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.LICENSEE)) {
                        account.setLicensee((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ACCOUNT_NUMBER)) {
                        account.setAccountNumber((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.DATE_CREATED)) {
                        account.setDateCreated(convertFromSQLDateToJAVADate((java.sql.Date) queryValue));
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.BRANCH_CODE)) {
                        account.setBranchCode((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.BRANCH_NAME)) {
                        account.setBranchName((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ADVISER_CODE)) {
                        account.setAdviserCode((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ADVISER_NAME)) {
                        account.setAdviserName((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.GIVEN_NAME)) {
                        account.setGivenName((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.SURNAME)) {
                        account.setSurname((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.EMAIL)) {
                        account.setEmail((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ADDRESS1)) {
                        account.setAddress1((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ADDRESS2)) {
                        account.setAddress2((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ADDRESS3)) {
                        account.setAddress3((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.ADDRESS4)) {
                        account.setAddress4((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.POSTCODE)) {
                        account.setPostcode((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.PHONE_HOME)) {
                        account.setPhoneHome((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.PHONE_WORK)) {
                        account.setPhoneWork((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.MOBILE)) {
                        account.setMobile((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.HIN)) {
                        account.setHin((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RNAME1)) {
                        account.setrName1((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RNAME2)) {
                        account.setrName2((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RNAME3)) {
                        account.setrName3((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.DESIGNATION)) {
                        account.setDesignation((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RADDRESS1)) {
                        account.setrAddress1((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RADDRESS2)) {
                        account.setrAddress2((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RADDRESS3)) {
                        account.setrAddress3((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RADDRESS4)) {
                        account.setrAddress4((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RPOSTCODE)) {
                        account.setrPostCode((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.RISOCOUNTRY)) {
                        account.setrIsoCountry((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.CLIENT_CLASS)) {
                        account.setClientClass((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.CLIENT_TYPE)) {
                        account.setClientType((String) queryValue);
                    } else if (key.equalsIgnoreCase(CompliiRequestFields.GREETING)) {
                        account.setGreeting((String) queryValue);
                    }
                }
            }
            accounts.add(account);
        }
        return accounts;
    }

    private List<CompliiAccount> retrieveAccounts(String lastAccountSuccessfulPushTimestamp) {
        if (lastAccountSuccessfulPushTimestamp == null || lastAccountSuccessfulPushTimestamp.isEmpty()) {
            LOGGER.error("Complii - Last successful push of account is not present. Baseline is required");
            // perhaps first run
            return null;
        } else {
            String timeFilter = " and c.row_modified >= " + "'" + lastAccountSuccessfulPushTimestamp + "'";
            String accountQueryFromLastSuccessfulPush = compliiAccountQuery + timeFilter;
            if (configurationService.getValue(ConfigurationKey.COMPLII_TEST_MODE)) {
                // limit the number of records so as to make it easier for testing in case there are too many records returned
                accountQueryFromLastSuccessfulPush += " limit 2";
            }
            accountQueryFromLastSuccessfulPush += ";";
            List<Map<String, Object>> resultRows = dataAccessHelper.getData(accountQueryFromLastSuccessfulPush);
            return mapAccountQueryResponse(resultRows);
        }
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
    public void setDataAccessHelper(DataAccessHelper dataAccessHelper) {
        this.dataAccessHelper = dataAccessHelper;
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    private boolean isAccountServiceEnabled() {
        Boolean enable = configurationService.getValue(ConfigurationKey.COMPLII_ACCOUNT_SERVICE_ENABLED);
        return enable != null && enable;
    }
}
