package com.gbst.complii.utils;

/**
 * The Order/Account/Holding key names in the Order/Account/Holding Model which are used to construct the web
 * service post order/account/holding requests and the header which contains the authentication data
 */
public class CompliiRequestFields {

    // the header which contains the authentication data
    public static final String APPKEY = "AppKey";
    public static final String LICENSEE_TOKEN = "LicenseeToken";
    public static final String AUTHORIZATION = "Authorization";

    /*** The Order key names in the Order Model which are used to construct the web service post order request ***/
    public static final String ORDER_ID = "OrderID";
    public static final String LICENSEE = "Licensee";
    public static final String DATA_SOURCE = "DataSource";
    public static final String ACCOUNT_ID = "AccountID";
    public static final String ADVISER_CODE = "AdviserCode";
    public static final String ORDER_NUMBER = "OrderNumber";
    public static final String DATE_CREATED = "DateCreated";
    public static final String TIMESTAMP = "Timestamp";
    public static final String BUYSELL = "BuySell";
    public static final String STOCK = "Stock";
    public static final String QTY = "Qty";
    public static final String PRICE = "Price";
    public static final String BROKERAGE = "Brokerage";
    public static final String BASIS_OF_ADVICE = "BasisOfAdvice";
    public static final String ADVICE_GIVEN = "AdviceGiven";
    public static final String OUTSTANDING = "Outstanding";
    public static final String ACTION_TAKEN = "ActionTaken";
    public static final String ORDER_TYPE = "OrderType";
    public static final String PRINCIPAL_APPROVED = "PrincipalApproved";
    public static final String PRINCIPAL_APPROVED_DATE = "PrincipalApprovedDate";
    public static final String PRINCIPAL_APPROVED_BY = "PrincipalApprovedBy";
    public static final String CREATED_DATE = "CreatedDate";
    public static final String CREATED_BY = "CreatedBy";
    public static final String UPDATED_DATE = "UpdatedDate";
    public static final String UPDATED_BY = "UpdatedBy";
    public static final String OFFER_LETTER_ORDER = "OfferLetterOrder";
    public static final String ORDER_TAKER = "OrderTaker";
    public static final String ORDER_GIVER = "OrderGiver";
    public static final String ORDER_TIME_TEXT = "OrderTimeText";
    public static final String DECLARATIONS = "Declarations";
    public static final String CONTRACT_NOTE_STATUS = "ContractNoteStatus";
    public static final String EXTERNAL_CONTRACT_NOTE_ID = "ExternalContractNoteID";
    public static final String ORIGINAL_ORDER_ID = "OriginalOrderID";
    public static final String TRADE_POSITION_ID = "TradePositionID";
    public static final String BROKERAGE_GST = "BrokerageGST";
    public static final String CURRENCY = "Currency";
    public static final String TIMEZONE = "Timezone";
    public static final String BROKERAGE_DESC = "BrokerageDesc";
    public static final String MERGE_STATUS = "MergeStatus";
    public static final String COMPLII_IMPORTED_DATE = "CompliiImportedDate";
    public static final String INTERNAL_ORDER_NUMBER = "InternalOrderNumber";
    public static final String INTERNAL_DATA = "InternalData";
    public static final String MARKET_ID = "MarketID";

    /*** The Account key names in the Account Model which are used to construct the web service post Account request ***/
    // account id
    // licensee field
    public static final String ACCOUNT_NUMBER = "AccountNumber";
    // date created
    public static final String BRANCH_CODE = "BranchCode";
    public static final String BRANCH_NAME = "BranchName";
    // adviser code
    public static final String ADVISER_NAME = "AdviserName";
    public static final String GIVEN_NAME = "Given";
    public static final String SURNAME = "Surname";
    public static final String EMAIL = "Email";
    public static final String ADDRESS1 = "Address1";
    public static final String ADDRESS2 = "Address2";
    public static final String ADDRESS3 = "Address3";
    public static final String ADDRESS4 = "Address4";
    public static final String POSTCODE = "Postcode";
    public static final String PHONE_HOME = "PhoneHome";
    public static final String PHONE_WORK = "PhoneWork";
    public static final String MOBILE = "Mobile";
    public static final String HIN = "HIN";
    public static final String RNAME1 = "RName1";
    public static final String RNAME2 = "RName2";
    public static final String RNAME3 = "RName3";
    public static final String DESIGNATION = "Designation";
    public static final String RADDRESS1 = "RAddress1";
    public static final String RADDRESS2 = "RAddress2";
    public static final String RADDRESS3 = "RAddress3";
    public static final String RADDRESS4 = "RAddress4";
    public static final String RPOSTCODE = "RAddress5";
    public static final String RISOCOUNTRY = "RIsoCountry";
    public static final String CLIENT_CLASS = "ClientClass";
    public static final String CLIENT_TYPE = "ClientType";
    public static final String GREETING = "Greeting";

    /*** The holding key names in the Holding Model which are used to construct the web service post Holding request ***/
    public static final String HOLDINGS_ID = "HoldingsID";
    // licensee
    // account number
    public static final String SECURITY = "Security";
    public static final String MARKET = "Market";
    public static final String HOLDINGS_DATE = "HoldingsDate";
    public static final String SPONSORED_VOLUME = "SponsoredVolume";
    public static final String UNSPONSORED_VOLUME = "UnsponsoredVolume";
    public static final String PRICE_ON_DATE = "PriceOnDate";
    public static final String MARKET_VALUE_ON_DATE = "MarketValueOnDate";

    public static final String DATE_FROM = "DateFrom";
    public static final String DATE_TO = "DateTo";
    public static final String COMPLII_IMPORTED = "CompliiImported";
    public static final String SHOW_MOST_RECENT_HOLDINGS_ONLY = "ShowMostRecentHoldingsOnly";


}
