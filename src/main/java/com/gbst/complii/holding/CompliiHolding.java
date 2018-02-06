package com.gbst.complii.holding;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public class CompliiHolding {

    private Integer holdingsID;
    private String licensee; // Identifier for Licensee that the orders relates to. Only authorized licensees can be set
    private String accountNumber;
    private String security;
    private String market;
    private Date holdingsDate;
    private Integer sponsoredVolume;
    private Integer unsponsoredVolume;
    private BigDecimal priceOnDate;
    private BigDecimal marketValueOnDate;

    public CompliiHolding() {
    }

    public CompliiHolding(Integer holdingsID, String licensee, String accountNumber, String security, String market,
                          Date holdingsDate, Integer sponsoredVolume, Integer unsponsoredVolume,
                          BigDecimal priceOnDate, BigDecimal marketValueOnDate) {
        this.holdingsID = holdingsID;
        this.licensee = licensee;
        this.accountNumber = accountNumber;
        this.security = security;
        this.market = market;
        this.holdingsDate = holdingsDate;
        this.sponsoredVolume = sponsoredVolume;
        this.unsponsoredVolume = unsponsoredVolume;
        this.priceOnDate = priceOnDate;
        this.marketValueOnDate = marketValueOnDate;
    }

    @JsonProperty(value="HoldingsID")
    public Integer getHoldingsID() {
        return holdingsID;
    }

    public void setHoldingsID(Integer accountID) {
        this.holdingsID = accountID;
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

    @JsonProperty(value="HoldingsDate")
    public Date getHoldingsDate() {
        return holdingsDate;
    }

    public void setHoldingsDate(Date holdingsDate) {
        this.holdingsDate = holdingsDate;
    }

    @JsonProperty(value="SponsoredVolume")
    public Integer getSponsoredVolume() {
        return sponsoredVolume;
    }

    public void setSponsoredVolume(Integer sponsoredVolume) {
        this.sponsoredVolume = sponsoredVolume;
    }

    @JsonProperty(value="UnsponsoredVolume")
    public Integer getUnsponsoredVolume() {
        return unsponsoredVolume;
    }

    public void setUnsponsoredVolume(Integer unsponsoredVolume) {
        this.unsponsoredVolume = unsponsoredVolume;
    }

    @JsonProperty(value="PriceOnDate")
    public BigDecimal getPriceOnDate() {
        return priceOnDate;
    }

    public void setPriceOnDate(BigDecimal priceOnDate) {
        this.priceOnDate = priceOnDate;
    }

    @JsonProperty(value="MarketValueOnDate")
    public BigDecimal getMarketValueOnDate() {
        return marketValueOnDate;
    }

    public void setMarketValueOnDate(BigDecimal marketValueOnDate) {
        this.marketValueOnDate = marketValueOnDate;
    }

    @Override
    public String toString() {
        return "CompliiHolding{" +
                "holdingsID=" + holdingsID +
                ", licensee='" + licensee + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", security='" + security + '\'' +
                ", market='" + market + '\'' +
                ", holdingsDate=" + holdingsDate +
                ", sponsoredVolume=" + sponsoredVolume +
                ", unsponsoredVolume=" + unsponsoredVolume +
                ", priceOnDate=" + priceOnDate +
                ", marketValueOnDate=" + marketValueOnDate +
                '}';
    }
}
