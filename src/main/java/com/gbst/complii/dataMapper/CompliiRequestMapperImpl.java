package com.gbst.complii.dataMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbst.complii.account.CompliiAccount;
import com.gbst.complii.account.CompliiAccountSearch;
import com.gbst.complii.holding.CompliiHolding;
import com.gbst.complii.holding.CompliiHoldingSearch;
import com.gbst.complii.orders.CompliiOrder;
import com.gbst.complii.orders.CompliiOrderSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class CompliiRequestMapperImpl implements CompliiRequestMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompliiRequestMapperImpl.class);

    @Override
    public String mapBulkPostOrderRequest(List<CompliiOrder> orders) {
        String jsonRequestStr = createJsonRequestDataForBulkRequest(orders);
        LOGGER.debug("Complii - Bulk Post Order Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    @Override
    public String mapBulkPostAccountRequest(List<CompliiAccount> accounts) {
        String jsonRequestStr = createJsonRequestDataForBulkRequest(accounts);
        LOGGER.debug("Complii - Bulk Post Account Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    @Override
    public String mapBulkPostHoldingRequest(List<CompliiHolding> holdings) {
        String jsonRequestStr = createJsonRequestDataForBulkRequest(holdings);
        LOGGER.debug("Complii - Bulk Post Holding Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    @Override
    public String mapSearchOrderRequest(CompliiOrderSearch searchCriteria) {
        String jsonRequestStr = createJsonRequestDataForSingleRequest(searchCriteria);
        LOGGER.debug("Complii - Search Order Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    @Override
    public String mapSearchAccountRequest(CompliiAccountSearch searchCriteria) {
        String jsonRequestStr = createJsonRequestDataForSingleRequest(searchCriteria);
        LOGGER.debug("Complii - Search Account Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    @Override
    public String mapSearchHoldingRequest(CompliiHoldingSearch searchCriteria) {
        String jsonRequestStr = createJsonRequestDataForSingleRequest(searchCriteria);
        LOGGER.debug("Complii - Search Account Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    @Override
    public String mapSinglePostOrderRequest(CompliiOrder order) {
        String jsonRequestStr = createJsonRequestDataForSingleRequest(order);
        LOGGER.debug("Complii - Single Post Order Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    @Override
    public String mapSinglePostAccountRequest(CompliiAccount account) {
        String jsonRequestStr = createJsonRequestDataForSingleRequest(account);
        LOGGER.debug("Complii - Single Post Account Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    @Override
    public String mapSinglePostHoldingRequest(CompliiHolding holding) {
        String jsonRequestStr = createJsonRequestDataForSingleRequest(holding);
        LOGGER.debug("Complii - Bulk Post Holding Json Request : {}", jsonRequestStr);
        return jsonRequestStr;
    }

    private String createJsonRequestDataForBulkRequest(List<? extends Object> objects) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(objects);
        } catch (IOException e) {
            throw new RuntimeException("Error while converting objects to json requset", e);
        }
    }

    private String createJsonRequestDataForSingleRequest(Object Object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(Object);
        } catch (IOException e) {
            throw new RuntimeException("Error while converting object to json requset", e);
        }
    }
}
