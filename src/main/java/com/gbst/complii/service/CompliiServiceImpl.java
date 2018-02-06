package com.gbst.complii.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbst.complii.account.AccountPostResult;
import com.gbst.complii.account.BulkAccountPostResult;
import com.gbst.complii.account.CompliiAccount;
import com.gbst.complii.account.CompliiAccountSearch;
import com.gbst.complii.configuration.ConfigurationKey;
import com.gbst.complii.configuration.ConfigurationService;
import com.gbst.complii.dataMapper.CompliiRequestMapper;
import com.gbst.complii.dataMapper.CompliiRequestMapperImpl;
import com.gbst.complii.holding.BulkHoldingPostResult;
import com.gbst.complii.holding.CompliiHolding;
import com.gbst.complii.holding.CompliiHoldingSearch;
import com.gbst.complii.holding.HoldingPostResult;
import com.gbst.complii.orders.CompliiBulkPostTransactionResult;
import com.gbst.complii.orders.CompliiOrder;
import com.gbst.complii.orders.CompliiOrderSearch;
import com.gbst.complii.orders.OrderPostResult;
import com.gbst.complii.utils.CompliiRequestFields;
import com.gbst.complii.utils.CompliiRequestHeader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The REST implementation for Complii Service
 */
@Component
public class CompliiServiceImpl implements CompliiService {

    private static final Logger LOGGER = LoggerFactory.getLogger("CompliiServiceImpl");
    private ConfigurationService configurationService;
    private CompliiRequestHeader compliiRequestHeader;
    private UriBuilder uriBuilder;
    private final Client client;
    private CompliiRequestMapper compliiRequestMapper;
    private Map<String, String> apiResourceMap;

    @Autowired
    public CompliiServiceImpl(ClientConfig clientConfig, ConfigurationService cs) {
        configurationService = cs;
        // construct the header which generally can be used for all request so that we don't have to query the configuration for each of the request
        compliiRequestHeader = new CompliiRequestHeader();
        compliiRequestHeader.setLicensee((String) configurationService.getValue(ConfigurationKey.COMPLII_APPKEY));
        compliiRequestHeader.setLicenseeToken((String) configurationService.getValue(ConfigurationKey.COMPLII_LICENSEE_TOKEN));
        compliiRequestHeader.setAuthorization((String) configurationService.getValue(ConfigurationKey.COMPLII_AUTHORIZATION));
        // construct the rest resources
        String compliiURL = (String) configurationService.getValue(ConfigurationKey.COMPLII_URL);
        if (configurationService.getValue(ConfigurationKey.COMPLII_TEST_MODE)) {
            // test mode ie testing with test server, just ignore the ssl cert to make the testing easier as complii can change the test server
            try {
                TrustManager[] trustManager = new X509TrustManager[]{new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        LOGGER.debug("getAcceptedIssuers");
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        LOGGER.debug("checkClientTrusted authType = {}", authType);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        LOGGER.debug("checkServerTrusted authType = {}", authType);
                    }
                }};
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustManager, null);
                if (sslContext != null) {
                    clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(null, sslContext));
                }
            } catch (Exception e) {
                LOGGER.error("Error when trying to ignore the validity of the ssl cert", e);
            }
        }
        client = Client.create(clientConfig);
        uriBuilder = UriBuilder.fromUri(compliiURL);
        // construct the api resource map
        constructApiResourceMap();
        compliiRequestMapper = new CompliiRequestMapperImpl();
    }

    private void constructApiResourceMap() {
        apiResourceMap = new HashMap<String, String>();
        apiResourceMap.put(ConfigurationKey.COMPLII_POST_ORDER_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_POST_ORDER_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_BULK_POST_ORDER_TRANSACTION_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_BULK_POST_ORDER_TRANSACTION_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_SEARCH_ORDER_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_SEARCH_ORDER_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_POST_ACCOUNT_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_POST_ACCOUNT_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_BULK_POST_ACCOUNT_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_BULK_POST_ACCOUNT_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_SEARCH_ACCOUNT_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_SEARCH_ACCOUNT_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_POST_HOLDING_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_POST_HOLDING_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_BULK_POST_HOLDING_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_BULK_POST_HOLDING_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_SEARCH_HOLDING_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_SEARCH_HOLDING_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_BULK_POST_ORDER_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_BULK_POST_ORDER_RESOURCE));
        apiResourceMap.put(ConfigurationKey.COMPLII_INTERNAL_POST_ORDER_RESOURCE.name(), (String) configurationService.getValue(ConfigurationKey.COMPLII_INTERNAL_POST_ORDER_RESOURCE));
    }

    @Override
    public int sendOrder(CompliiOrder order) {
        LOGGER.debug("Complii - Sending this order : {}", order);
        GenericType<Integer> type = new GenericType<Integer>() {};
        String method = apiResourceMap.get(ConfigurationKey.COMPLII_INTERNAL_POST_ORDER_RESOURCE.name());
        WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
        String orderJsonRequest = compliiRequestMapper.mapSinglePostOrderRequest(order);
        int returnCode = resource
                .type(MediaType.APPLICATION_JSON_TYPE)
                .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                .post(type, orderJsonRequest);
        LOGGER.debug("Complii - Posting the order returns {}", returnCode);
        return returnCode;
    }

    @Override
    public OrderPostResult postOrder(CompliiOrder order) {
        try {
            LOGGER.debug("Complii - Posting this order : {}", order);
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_POST_ORDER_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String orderJsonRequest = compliiRequestMapper.mapSinglePostOrderRequest(order);
            String postingResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, orderJsonRequest);
            LOGGER.debug("Complii - Posting the order returns json {}", postingResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            OrderPostResult orderPostResult = mapper.readValue(postingResult, OrderPostResult.class);
            LOGGER.debug("Complii - Map json response to OrderPostResult object returns ", orderPostResult);
            return orderPostResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean bulkPostOrder(List<CompliiOrder> orders) {
        LOGGER.debug("Complii - Posting {} order", orders.size());
        GenericType<Boolean> type = new GenericType<Boolean>() {};
        String method = apiResourceMap.get(ConfigurationKey.COMPLII_BULK_POST_ORDER_RESOURCE.name());
        WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
        String orderJsonRequest = compliiRequestMapper.mapBulkPostOrderRequest(orders);
        boolean returnResult = resource
                .type(MediaType.APPLICATION_JSON_TYPE)
                .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                .post(type, orderJsonRequest);
        LOGGER.debug("Complii - BulkPost returns {}", returnResult);
        return returnResult;
    }

    @Override
    public CompliiBulkPostTransactionResult bulkPostOrderTransaction(List<CompliiOrder> orders) {
        try {
            LOGGER.debug("Complii - Posting {} order using Bulk Post Order Transaction", orders.size());
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_BULK_POST_ORDER_TRANSACTION_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String orderJsonRequest = compliiRequestMapper.mapBulkPostOrderRequest(orders);
            String postingResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, orderJsonRequest);
            LOGGER.debug("Complii - BulkPostTransaction returns {}", postingResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            CompliiBulkPostTransactionResult orderPostResult = mapper.readValue(postingResult, CompliiBulkPostTransactionResult.class);
            LOGGER.debug("Complii - Map json response to CompliiBulkPostTransactionResult object returns ", orderPostResult);
            return orderPostResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CompliiOrder> searchOrder(CompliiOrderSearch compliiOrderSearch) {
        try {
            LOGGER.debug("Complii - Searching order criteria: {} ", compliiOrderSearch);
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_SEARCH_ORDER_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String searchOrderJsonRequest = compliiRequestMapper.mapSearchOrderRequest(compliiOrderSearch);
            String returnResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, searchOrderJsonRequest);
            LOGGER.debug("Complii - Search Order returns {}", returnResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CompliiOrder> orders = mapper.readValue(returnResult, new TypeReference<List<CompliiOrder>>() {
            });
            LOGGER.debug("Map json response list order list returns ", orders);
            return orders;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AccountPostResult postAccount(CompliiAccount account) {
        try {
            LOGGER.debug("Complii - Posting this account : {}", account);
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_POST_ACCOUNT_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String accountJsonRequest = compliiRequestMapper.mapSinglePostAccountRequest(account);
            String postingResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, accountJsonRequest);
            LOGGER.debug("Complii - Posting the account returns {}", postingResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            AccountPostResult accountPostResult = mapper.readValue(postingResult, AccountPostResult.class);
            LOGGER.debug("Complii - Map json response to AccountPostResult object returns ", accountPostResult);
            return accountPostResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BulkAccountPostResult bulkPostAccount(List<CompliiAccount> accounts) {
        try {
            LOGGER.debug("Complii - Posting this bulk account : {}", accounts);
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_BULK_POST_ACCOUNT_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String accountJsonRequest = compliiRequestMapper.mapBulkPostAccountRequest(accounts);
            String postingResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, accountJsonRequest);
            LOGGER.debug("Complii - Bulk Posting account returns {}", postingResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            BulkAccountPostResult accountPostResult = mapper.readValue(postingResult, BulkAccountPostResult.class);
            LOGGER.debug("Complii - Map json response to BulkAccountPostResult object returns ", accountPostResult);
            return accountPostResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CompliiAccount> searchAccounts(CompliiAccountSearch compliiAccountSearch) {
        try {
            LOGGER.debug("Complii - Searching account criteria: {} ", compliiAccountSearch);
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_SEARCH_ACCOUNT_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String searchAccountJsonRequest = compliiRequestMapper.mapSearchAccountRequest(compliiAccountSearch);
            String returnResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, searchAccountJsonRequest);
            LOGGER.debug("Complii - Search Account returns {}", returnResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CompliiAccount> acccounts = mapper.readValue(returnResult, new TypeReference<List<CompliiAccount>>() {
            });
            LOGGER.debug("Complii - Map json response to account list returns ", acccounts);
            return acccounts;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HoldingPostResult postHoldings(CompliiHolding holding) {
        try {
            LOGGER.debug("Complii - Posting this holdings : {}", holding);
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_POST_HOLDING_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String holdingJsonRequest = compliiRequestMapper.mapSinglePostHoldingRequest(holding);
            String postingResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, holdingJsonRequest);
            LOGGER.debug("Complii - Posting the holdings returns {}", postingResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            HoldingPostResult holdingPostResult = mapper.readValue(postingResult, HoldingPostResult.class);
            LOGGER.debug("Complii - Map json response to HoldingPostResult object returns ", holdingPostResult);
            return holdingPostResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BulkHoldingPostResult bulkPostHoldings(List<CompliiHolding> holdings) {
        try {
            LOGGER.debug("Complii - Posting this bulk holdings : {}", holdings);
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_BULK_POST_HOLDING_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String holdingJsonRequest = compliiRequestMapper.mapBulkPostHoldingRequest(holdings);
            String postingResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, holdingJsonRequest);
            LOGGER.debug("Complii - Bulk Posting holdings returns {}", postingResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            BulkHoldingPostResult holdingPostResult = mapper.readValue(postingResult, BulkHoldingPostResult.class);
            LOGGER.debug("Complii - Map json response to BulkHoldingPostResult object returns ", holdingPostResult);
            return holdingPostResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CompliiHolding> searchHoldings(CompliiHoldingSearch compliiHoldingSearch) {
        try {
            LOGGER.debug("Complii - Searching holding criteria: {} ", compliiHoldingSearch);
            GenericType<String> type = new GenericType<String>() {};
            String method = apiResourceMap.get(ConfigurationKey.COMPLII_SEARCH_HOLDING_RESOURCE.name());
            WebResource resource = client.resource(uriBuilder.clone().segment(method).build());
            String searchHoldingJsonRequest = compliiRequestMapper.mapSearchHoldingRequest(compliiHoldingSearch);
            String returnResult = resource
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .header(CompliiRequestFields.APPKEY, compliiRequestHeader.getLicensee())
                    .header(CompliiRequestFields.LICENSEE_TOKEN, compliiRequestHeader.getLicenseeToken())
                    .header(CompliiRequestFields.AUTHORIZATION, compliiRequestHeader.getAuthorization())
                    .post(type, searchHoldingJsonRequest);
            LOGGER.debug("Complii - Search Holdings returns {}", returnResult);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CompliiHolding> holdings = mapper.readValue(returnResult, new TypeReference<List<CompliiHolding>>() {
            });
            LOGGER.debug("Complii - Map json response to holding list returns ", holdings);
            return holdings;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
