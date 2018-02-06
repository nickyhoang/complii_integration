package com.gbst.complii.service;

import com.gbst.complii.account.AccountPostResult;
import com.gbst.complii.account.BulkAccountPostResult;
import com.gbst.complii.account.CompliiAccount;
import com.gbst.complii.account.CompliiAccountSearch;
import com.gbst.complii.holding.BulkHoldingPostResult;
import com.gbst.complii.holding.CompliiHolding;
import com.gbst.complii.holding.CompliiHoldingSearch;
import com.gbst.complii.holding.HoldingPostResult;
import com.gbst.complii.orders.CompliiBulkPostTransactionResult;
import com.gbst.complii.orders.CompliiOrder;
import com.gbst.complii.orders.CompliiOrderSearch;
import com.gbst.complii.orders.OrderPostResult;
import java.util.List;

/**
 * Provide services to Complii system
 */
public interface CompliiService {

    /**************************** Order Services *******************************/

    /**
     * Posting an Order to Complii
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-Orders
     * This method, as advised by Complii, is used internally. The response doesn't seem to be meaningful and good enough for us
     * @param order : the order to be posted
     * @return 1 if the order is processed successfully and other value otherwise.
     */
    public int sendOrder(CompliiOrder order);

    /**
     * Bulk posting a list of order to Complii
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-BulkPost
     * @param orders : a list of order to be posted to complii
     * @return true if the all orders are processed successfully in Complii and false otherwise
     */
    public boolean bulkPostOrder (List<CompliiOrder> orders);

    /**
     * Posting an Order to Complii
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-PostOrder
     * @param order : the order to be posted
     * @return 1 if the order is processed successfully and other value otherwise.
     */
    public OrderPostResult postOrder(CompliiOrder order);

    /**
     * This call provides Transactional support for Bulk Order Posting.
     * This is to ensure if one order fails that it rolls back the list and returns back the error  the last successful order number
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-BulkPostTransaction
     * @param orders : a list of order to be posted to complii
     * @return the bulk post transaction response
     */
    public CompliiBulkPostTransactionResult bulkPostOrderTransaction (List<CompliiOrder> orders);

    /**
     * Search orders in Complii System
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-OrderSearch
     * @param compliiOrderSearch : the search criteria
     * @return a list of complii orders or null/empty if no orders are found
     */
    public List<CompliiOrder> searchOrder (CompliiOrderSearch compliiOrderSearch);

    /**************************** Account Services *******************************/

    /**
     * Posting an Account to Complii
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-PostAccount
     * @param account : the account to be posted
     * @return the AccountPostResult
     */
    public AccountPostResult postAccount(CompliiAccount account);

    /**
     * Posting a list of Account to Complii
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-BulkPostAccount
     * @param accounts : Collection of Accounts to save. Limited to 100 at a time.
     * @return the BulkAccountPostResult
     */
    public BulkAccountPostResult bulkPostAccount(List<CompliiAccount> accounts);

    /**
     * Search accounts in Complii System
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-AccountSearch
     * @param compliiAccountSearch : the search criteria
     * @return a list of complii accounts or null/empty if no accounts are found
     */
    public List<CompliiAccount> searchAccounts (CompliiAccountSearch compliiAccountSearch);


    /**************************** Holding Services *******************************/

    /**
     * Posting an Holding to Complii
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-PostHolding
     * @param holding : the holding to be posted
     * @return the AccountPostResult
     */
    public HoldingPostResult postHoldings(CompliiHolding holding);

    /**
     * Posting a list of Holdings to Complii
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-BulkPostHolding
     * @param holdings : Collection of Accounts to save. Limited to 100 at a time.
     * @return the BulkAccountPostResult
     */
    public BulkHoldingPostResult bulkPostHoldings(List<CompliiHolding> holdings);

    /**
     * Search holdings in Complii System
     * https://license-api-uat.complii.com.au/Help/Api/POST-api-HoldingSearch
     * @param compliiHoldingSearch : the search criteria
     * @return a list of complii holdings or null/empty if no holdings are found
     */
    public List<CompliiHolding> searchHoldings(CompliiHoldingSearch compliiHoldingSearch);
}
