package com.gbst.complii.holding;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class CompliiHoldingSearch {

    private String licensee; // Identifier for Licensee that the orders relates to. Only authorized licensees can be set
    private String accountNumber; // The unique account number for publicly identifying this account with the trade data source
    private String security;
    private String market;
    private Date dateFrom; // Date to search From (Holding Date)
    private Date dateTo; // Date to search To (Holding Date)
    private Boolean showMostRecentHoldingsOnly; // Controls if search returns only most recent holdings (true by default)

    public CompliiHoldingSearch() {
    }

    public CompliiHoldingSearch(String licensee, String accountNumber, String security, String market, Date dateFrom, Date dateTo, Boolean showMostRecentHoldingsOnly) {
        this.licensee = licensee;
        this.accountNumber = accountNumber;
        this.security = security;
        this.market = market;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.showMostRecentHoldingsOnly = showMostRecentHoldingsOnly;
    }

    @JsonProperty(value="Licensee")
    public String getLicensee() {
        return licensee;
    }

    public void setLicensee(String licensee) {
        this.licensee = licensee;
    }

    @JsonProperty(value="AccountNumber")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @JsonProperty(value="Security")
    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    @JsonProperty(value="Market")
    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
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

    @JsonProperty(value="ShowMostRecentHoldingsOnly")
    public Boolean getShowMostRecentHoldingsOnly() {
        return showMostRecentHoldingsOnly;
    }

    public void setShowMostRecentHoldingsOnly(Boolean showMostRecentHoldingsOnly) {
        this.showMostRecentHoldingsOnly = showMostRecentHoldingsOnly;
    }

    @Override
    public String toString() {
        return "CompliiHoldingSearch{" +
                "licensee='" + licensee + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", security='" + security + '\'' +
                ", market='" + market + '\'' +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", showMostRecentHoldingsOnly=" + showMostRecentHoldingsOnly +
                '}';
    }
}
