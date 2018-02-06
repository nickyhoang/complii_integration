package com.gbst.complii.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class CompliiOrderSearch {

    private String licensee; // Identifier for Licensee that the orders relates to. Only authorized licensees can be set
    private String orderNumber; // The unique order number for publicly identifying this order with the trade data source
    private String stock; // The unique security code for the Order (i.e. "BHP" or "ANZ")
    private String accountID; // The Account Number for the Client linked to the Order
    private String adviserCode; // The Adviser's unique code
    private String orderType; // The Type of Order - Client or Market (Client order is requested Order, Market is actual trade)
    private Integer mergeStatus; // Used to identify if the Order needs to be merged with another importer before the data is ready. The MergeStatus number is unique.
    private Boolean compliiImported; // Complii Internal Field - the date the order was imported into Complii feeds
    private Date dateFrom; // Date to search From (Order CreatedDate)
    private Date dateTo; // Date to search To (Order CreatedDate)
    private String marketID; // The unique identifier for the Market this Order's security belongs to. Typically should be ASX. AOM is for Option Orders. Use ! in front of Market code to exclude

    @JsonProperty(value="Licensee")
    public String getLicensee() {
        return licensee;
    }

    public void setLicensee(String licensee) {
        this.licensee = licensee;
    }

    @JsonProperty(value="OrderNumber")
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @JsonProperty(value="Stock")
    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
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

    @JsonProperty(value="OrderType")
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @JsonProperty(value="MergeStatus")
    public Integer getMergeStatus() {
        return mergeStatus;
    }

    public void setMergeStatus(Integer mergeStatus) {
        this.mergeStatus = mergeStatus;
    }

    @JsonProperty(value="CompliiImported")
    public Boolean getCompliiImported() {
        return compliiImported;
    }

    public void setCompliiImported(Boolean compliiImported) {
        compliiImported = compliiImported;
    }

    @JsonProperty(value="DateFrom")
    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    @JsonProperty(value="DateTo")
    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
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
        return "CompliiOrderSearch{" +
                "licensee='" + licensee + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", stock='" + stock + '\'' +
                ", accountID='" + accountID + '\'' +
                ", adviserCode='" + adviserCode + '\'' +
                ", orderType='" + orderType + '\'' +
                ", mergeStatus=" + mergeStatus +
                ", compliiImported=" + compliiImported +
                ", dateFrom=" + dateFrom +
                ", dateTo='" + dateTo + '\'' +
                ", marketID='" + marketID + '\'' +
                '}';
    }
}
