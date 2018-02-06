package com.gbst.complii.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class CompliiAccountSearch {

    private String licensee; // Identifier for Licensee that the orders relates to. Only authorized licensees can be set
    private String accountNumber; // The unique account number for publicly identifying this account with the trade data source
    private Date dateFrom; // Date to search From (Account CreatedDate)
    private Date dateTo; // Date to search To (Account CreatedDate)
    private String branchCode;
    private String adviserCode;
    private String givenName;
    private String surname;
    private String email;
    private String phoneHome;
    private String phoneWork;
    private String mobile;
    private String hin;
    private String rIsoCountry;
    private String clientClass;
    private String clientType;

    public CompliiAccountSearch() {
    }

    public CompliiAccountSearch(String licensee, String accountNumber, Date dateFrom, Date dateTo, String branchCode,
                                String adviserCode, String givenName, String surname, String email, String phoneHome,
                                String phoneWork, String mobile, String hin, String rIsoCountry, String clientClass, String clientType) {
        this.licensee = licensee;
        this.accountNumber = accountNumber;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.branchCode = branchCode;
        this.adviserCode = adviserCode;
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
        this.phoneHome = phoneHome;
        this.phoneWork = phoneWork;
        this.mobile = mobile;
        this.hin = hin;
        this.rIsoCountry = rIsoCountry;
        this.clientClass = clientClass;
        this.clientType = clientType;
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

    @JsonProperty(value="BranchCode")
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    @JsonProperty(value="AdviserCode")
    public String getAdviserCode() {
        return adviserCode;
    }

    public void setAdviserCode(String adviserCode) {
        this.adviserCode = adviserCode;
    }

    @JsonProperty(value="Given")
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @JsonProperty(value="Surname")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonProperty(value="Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty(value="PhoneHome")
    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    @JsonProperty(value="PhoneWork")
    public String getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    @JsonProperty(value="Mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonProperty(value="HIN")
    public String getHin() {
        return hin;
    }

    public void setHin(String hin) {
        this.hin = hin;
    }

    @JsonProperty(value="RIsoCountry")
    public String getrIsoCountry() {
        return rIsoCountry;
    }

    public void setrIsoCountry(String rIsoCountry) {
        this.rIsoCountry = rIsoCountry;
    }

    @JsonProperty(value="ClientClass")
    public String getClientClass() {
        return clientClass;
    }

    public void setClientClass(String clientClass) {
        this.clientClass = clientClass;
    }

    @JsonProperty(value="ClientType")
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    @Override
    public String toString() {
        return "CompliiAccountSearch{" +
                "licensee='" + licensee + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", branchCode='" + branchCode + '\'' +
                ", adviserCode='" + adviserCode + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneHome='" + phoneHome + '\'' +
                ", phoneWork='" + phoneWork + '\'' +
                ", mobile='" + mobile + '\'' +
                ", hin='" + hin + '\'' +
                ", rIsoCountry='" + rIsoCountry + '\'' +
                ", clientClass='" + clientClass + '\'' +
                ", clientType='" + clientType + '\'' +
                '}';
    }
}
