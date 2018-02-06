package com.gbst.complii.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public class CompliiOrder {

    private Integer orderId; // The unique internal identifier for Orders. Auto generated so does not require user to change.
    private String licensee; // Identifier for Licensee that the orders relates to. Only authorized licensees can be set
    private String dataSource; // The data source pushing Orders into the API. For example "IOSPlus"
    private String accountID; // The Account Number for the Client linked to the Order
    private String adviserCode; // The Adviser's unique code
    private String orderNumber; // The unique order number for publicly identifying this order with the trade data source
    private Date dateCreated; // The date the Order was created
    private Date timestamp; // The date the Order was sent into the API
    private String buySell; // Is the Order Trade for Buy or Sell (B or S)
    private String stock; // The unique security code for the Order (i.e. "BHP" or "ANZ")
    private Integer qty; // The Quantity or Volume of the Trade
    private BigDecimal price; // The Price per unit of trade
    private BigDecimal brokerage; // The brokerage on the trade (exc Gst)
    private String basisOfAdvice; // The basis of Advice for the Trade (i.e. NONE or PERSONAl)
    private String adviceGiven; // The details of the advice given
    private Boolean outstanding; // Used internally to detect if an Order is Outstanding. Leaving blank indicates it has not been processed yet.
    private String actionTaken; // Any action taken in relation to Advice
    private String orderType; // The Type of Order - Client or Market (Client order is requested Order, Market is actual trade)
    private Boolean principalApproved; // Internal field for Complii - Principal Account Trades)
    private Date principalApprovedDate; // Internal field for Complii - Principal Account Trades)
    private String principalApprovedBy; // Internal field for Complii - Principal Account Trades)
    private Date createdDate; // Internal Complii Field - for tracking Order creation date in API
    private String createdBy; // Internal Complii Field - for tracking Order creation user in API
    private Date updatedDate; // Internal Complii Field - for tracking Order update date in API
    private Integer updatedBy; // Internal Complii Field - for tracking Order update user in API
    private Boolean offerLetterOrder; // Internal Complii Field - for tracking if order is relating to an Offer Letter
    private String orderTaker; // The person who took the order (typically the Adviser)
    private String orderGiver; // The person who gave the order (typically the client)
    private String orderTimeText; // Specific time text of the order
    private String declarations; // Any optional declarations required
    private String contractNoteStatus; // Complii Internal Field - setting status of contract note
    private String externalContractNoteID; // Complii Internal Field - setting status of external contract note
    private String originalOrderID; // The Parent Order Number for this Order. Used like a tree node structure as Market Orders often belong within a Client Order.
    private String tradePositionID; // Complii Internal Field - trade position identifier.
    private BigDecimal brokerageGST; // Order Brokerage including GST
    private String currency; // The main currency used for the Trade
    private String timezone; // The timezone of the trade (i.e. AEST)
    private String brokerageDesc; // The description of the Brokerage
    private Integer mergeStatus; // Used to identify if the Order needs to be merged with another importer before the data is ready. The MergeStatus number is unique.
    private Date compliiImportedDate; // Complii Internal Field - the date the order was imported into Complii feeds
    private String internalOrderNumber; // Internal Order Number - some source systems require an internal order number to link with legacy systems (for example IOS Plus and IOS Classic)
    private String internalData; // Internal Data field - this is a MAX size string field that can store optional data from the Vendor. Recommend keeping the data small for performance.
    private String marketID; // The unique identifier for the Market this Order's security belongs to. Typically should be ASX. AOM is for Option Orders. Use ! in front of Market code to exclude

    @JsonProperty(value="OrderID")
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @JsonProperty(value="Licensee")
    public String getLicensee() {
        return licensee;
    }

    public void setLicensee(String licensee) {
        this.licensee = licensee;
    }

    @JsonProperty(value="DataSource")
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @JsonProperty(value="AccountID")
    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    @JsonProperty(value="AdviserCode")
    public String getAdviserCode() {
        return adviserCode;
    }

    public void setAdviserCode(String adviserCode) {
        this.adviserCode = adviserCode;
    }

    @JsonProperty(value="OrderNumber")
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @JsonProperty(value="DateCreated")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @JsonProperty(value="Timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty(value="BuySell")
    public String getBuySell() {
        return buySell;
    }

    public void setBuySell(String buySell) {
        this.buySell = buySell;
    }

    @JsonProperty(value="Stock")
    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    @JsonProperty(value="Qty")
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @JsonProperty(value="Price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @JsonProperty(value="Brokerage")
    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    @JsonProperty(value="BasisOfAdvice")
    public String getBasisOfAdvice() {
        return basisOfAdvice;
    }

    public void setBasisOfAdvice(String basisOfAdvice) {
        this.basisOfAdvice = basisOfAdvice;
    }

    @JsonProperty(value="AdviceGiven")
    public String getAdviceGiven() {
        return adviceGiven;
    }

    public void setAdviceGiven(String adviceGiven) {
        this.adviceGiven = adviceGiven;
    }

    @JsonProperty(value="Outstanding")
    public Boolean getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(Boolean outstanding) {
        this.outstanding = outstanding;
    }

    @JsonProperty(value="ActionTaken")
    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    @JsonProperty(value="OrderType")
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @JsonProperty(value="PrincipalApproved")
    public Boolean getPrincipalApproved() {
        return principalApproved;
    }

    public void setPrincipalApproved(Boolean principalApproved) {
        this.principalApproved = principalApproved;
    }

    @JsonProperty(value="PrincipalApprovedDate")
    public Date getPrincipalApprovedDate() {
        return principalApprovedDate;
    }

    public void setPrincipalApprovedDate(Date principalApprovedDate) {
        this.principalApprovedDate = principalApprovedDate;
    }

    @JsonProperty(value="PrincipalApprovedBy")
    public String getPrincipalApprovedBy() {
        return principalApprovedBy;
    }

    public void setPrincipalApprovedBy(String principalApprovedBy) {
        this.principalApprovedBy = principalApprovedBy;
    }

    @JsonProperty(value="CreatedDate")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @JsonProperty(value="CreatedBy")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty(value="UpdatedDate")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @JsonProperty(value="UpdatedBy")
    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    @JsonProperty(value="OfferLetterOrder")
    public Boolean getOfferLetterOrder() {
        return offerLetterOrder;
    }

    public void setOfferLetterOrder(Boolean offerLetterOrder) {
        this.offerLetterOrder = offerLetterOrder;
    }

    @JsonProperty(value="OrderTaker")
    public String getOrderTaker() {
        return orderTaker;
    }

    public void setOrderTaker(String orderTaker) {
        this.orderTaker = orderTaker;
    }

    @JsonProperty(value="OrderGiver")
    public String getOrderGiver() {
        return orderGiver;
    }

    public void setOrderGiver(String orderGiver) {
        this.orderGiver = orderGiver;
    }

    @JsonProperty(value="OrderTimeText")
    public String getOrderTimeText() {
        return orderTimeText;
    }

    public void setOrderTimeText(String orderTimeText) {
        this.orderTimeText = orderTimeText;
    }

    @JsonProperty(value="Declarations")
    public String getDeclarations() {
        return declarations;
    }

    public void setDeclarations(String declarations) {
        this.declarations = declarations;
    }

    @JsonProperty(value="ContractNoteStatus")
    public String getContractNoteStatus() {
        return contractNoteStatus;
    }

    public void setContractNoteStatus(String contractNoteStatus) {
        this.contractNoteStatus = contractNoteStatus;
    }

    @JsonProperty(value="ExternalContractNoteID")
    public String getExternalContractNoteID() {
        return externalContractNoteID;
    }

    public void setExternalContractNoteID(String externalContractNoteID) {
        this.externalContractNoteID = externalContractNoteID;
    }

    @JsonProperty(value="OriginalOrderID")
    public String getOriginalOrderID() {
        return originalOrderID;
    }

    public void setOriginalOrderID(String originalOrderID) {
        this.originalOrderID = originalOrderID;
    }

    @JsonProperty(value="TradePositionID")
    public String getTradePositionID() {
        return tradePositionID;
    }

    public void setTradePositionID(String tradePositionID) {
        this.tradePositionID = tradePositionID;
    }

    @JsonProperty(value="BrokerageGST")
    public BigDecimal getBrokerageGST() {
        return brokerageGST;
    }

    public void setBrokerageGST(BigDecimal brokerageGST) {
        this.brokerageGST = brokerageGST;
    }

    @JsonProperty(value="Currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty(value="Timezone")
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @JsonProperty(value="BrokerageDesc")
    public String getBrokerageDesc() {
        return brokerageDesc;
    }

    public void setBrokerageDesc(String brokerageDesc) {
        this.brokerageDesc = brokerageDesc;
    }

    @JsonProperty(value="MergeStatus")
    public Integer getMergeStatus() {
        return mergeStatus;
    }

    public void setMergeStatus(Integer mergeStatus) {
        this.mergeStatus = mergeStatus;
    }

    @JsonProperty(value="CompliiImportedDate")
    public Date getCompliiImportedDate() {
        return compliiImportedDate;
    }

    public void setCompliiImportedDate(Date compliiImportedDate) {
        this.compliiImportedDate = compliiImportedDate;
    }

    @JsonProperty(value="InternalOrderNumber")
    public String getInternalOrderNumber() {
        return internalOrderNumber;
    }

    public void setInternalOrderNumber(String internalOrderNumber) {
        this.internalOrderNumber = internalOrderNumber;
    }

    @JsonProperty(value="InternalData")
    public String getInternalData() {
        return internalData;
    }

    public void setInternalData(String internalData) {
        this.internalData = internalData;
    }

    @JsonProperty(value="MarketID")
    public String getMarketID() {
        return marketID;
    }

    public void setMarketID(String marketID) {
        this.marketID = marketID;
    }

    @Override
    public String toString() {
        return "CompliiOrder{" +
                "orderId=" + orderId +
                ", licensee='" + licensee + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", accountID='" + accountID + '\'' +
                ", adviserCode='" + adviserCode + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", dateCreated=" + dateCreated +
                ", timestamp=" + timestamp +
                ", buySell='" + buySell + '\'' +
                ", stock='" + stock + '\'' +
                ", qty=" + qty +
                ", price=" + price +
                ", brokerage=" + brokerage +
                ", basisOfAdvice='" + basisOfAdvice + '\'' +
                ", adviceGiven='" + adviceGiven + '\'' +
                ", outstanding=" + outstanding +
                ", actionTaken='" + actionTaken + '\'' +
                ", orderType='" + orderType + '\'' +
                ", principalApproved=" + principalApproved +
                ", principalApprovedDate=" + principalApprovedDate +
                ", principalApprovedBy='" + principalApprovedBy + '\'' +
                ", createdDate=" + createdDate +
                ", createdBy='" + createdBy + '\'' +
                ", updatedDate=" + updatedDate +
                ", updatedBy='" + updatedBy + '\'' +
                ", offerLetterOrder=" + offerLetterOrder +
                ", orderTaker='" + orderTaker + '\'' +
                ", orderGiver='" + orderGiver + '\'' +
                ", orderTimeText='" + orderTimeText + '\'' +
                ", declarations='" + declarations + '\'' +
                ", contractNoteStatus='" + contractNoteStatus + '\'' +
                ", externalContractNoteID='" + externalContractNoteID + '\'' +
                ", originalOrderID='" + originalOrderID + '\'' +
                ", tradePositionID='" + tradePositionID + '\'' +
                ", brokerageGST=" + brokerageGST +
                ", currency='" + currency + '\'' +
                ", timezone='" + timezone + '\'' +
                ", brokerageDesc='" + brokerageDesc + '\'' +
                ", mergeStatus=" + mergeStatus +
                ", compliiImportedDate=" + compliiImportedDate +
                ", internalOrderNumber='" + internalOrderNumber + '\'' +
                ", internalData='" + internalData + '\'' +
                ", marketID='" + marketID + '\'' +
                '}';
    }
}
