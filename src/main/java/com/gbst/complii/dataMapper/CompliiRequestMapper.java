package com.gbst.complii.dataMapper;

import com.gbst.complii.account.CompliiAccount;
import com.gbst.complii.account.CompliiAccountSearch;
import com.gbst.complii.holding.CompliiHolding;
import com.gbst.complii.holding.CompliiHoldingSearch;
import com.gbst.complii.orders.CompliiOrder;
import com.gbst.complii.orders.CompliiOrderSearch;

import java.util.List;

/**
 * The request mapper for Complii requests
 */
public interface CompliiRequestMapper {

    /**
     * Generating the order json request from the order object
     * @param orders the orders
     * @return the order json data
     */
    public String mapBulkPostOrderRequest(List<CompliiOrder> orders);

    /**
     * Generating the account json request from the account object
     * @param accounts the accounts
     * @return the account json data
     */
    public String mapBulkPostAccountRequest(List<CompliiAccount> accounts);

    /**
     * Generating the holding json request from the holding object
     * @param holdings the holdings
     * @return the holding json data
     */
    public String mapBulkPostHoldingRequest(List<CompliiHolding> holdings);

    /**
     * Generating the search order json request from the search criteria object
     * @param searchCriteria
     * @return the search order json data
     */
    public String mapSearchOrderRequest(CompliiOrderSearch searchCriteria);

    /**
     * Generating the search account json request from the search criteria object
     * @param searchCriteria
     * @return the search account json data
     */
    public String mapSearchAccountRequest(CompliiAccountSearch searchCriteria);

    /**
     * Generating the search holding json request from the search criteria object
     * @param searchCriteria
     * @return the search holding json data
     */
    public String mapSearchHoldingRequest(CompliiHoldingSearch searchCriteria);

    /**
     * Generating the order json request from the order object
     * @param order the orders
     * @return the order json data
     */
    public String mapSinglePostOrderRequest(CompliiOrder order);

    /**
     * Generating the account json request from the account object
     * @param account the accounts
     * @return the account json data
     */
    public String mapSinglePostAccountRequest(CompliiAccount account);

    /**
     * Generating the holding json request from the holding object
     * @param holding the holdings
     * @return the holding json data
     */
    public String mapSinglePostHoldingRequest(CompliiHolding holding);
}
