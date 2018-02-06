package com.gbst.complii.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class CompliiAccount {

    private Integer accountID;
    private String licensee; // Identifier for Licensee that the orders relates to. Only authorized licensees can be set
    private String accountNumber;
    private Date dateCreated;
    private String branchCode;
    private String branchName;
    private String adviserCode;
    private String adviserName;
    private String givenName;
    private String surname;
    private String email;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private String postcode;
    private String phoneHome;
    private String phoneWork;
    private String mobile;
    private String hin;
    private String rName1;
    private String rName2;
    private String rName3;
    private String designation;
    private String rAddress1;
    private String rAddress2;
    private String rAddress3;
    private String rAddress4;
    private String rPostCode;
    private String rIsoCountry;
    private String clientClass;
    private String clientType;
    private String greeting;

    public CompliiAccount() {
    }

    public CompliiAccount(Integer accountID, String licensee, String accountNumber,
                          Date dateCreated, String branchCode, String branchName, String adviserCode,
                          String adviserName, String givenName, String surname, String email, String address1,
                          String address2, String address3, String address4, String address5, String postcode,
                          String phoneHome, String phoneWork, String mobile, String hin, String rName1,
                          String rName2, String rName3, String designation, String rAddress1, String rAddress2,
                          String rAddress3, String rAddress4, String rPostCode, String rIsoCountry,
                          String clientClass, String clientType, String greeting) {
        this.accountID = accountID;
        this.licensee = licensee;
        this.accountNumber = accountNumber;
        this.dateCreated = dateCreated;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.adviserCode = adviserCode;
        this.adviserName = adviserName;
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.postcode = postcode;
        this.phoneHome = phoneHome;
        this.phoneWork = phoneWork;
        this.mobile = mobile;
        this.hin = hin;
        this.rName1 = rName1;
        this.rName2 = rName2;
        this.rName3 = rName3;
        this.designation = designation;
        this.rAddress1 = rAddress1;
        this.rAddress2 = rAddress2;
        this.rAddress3 = rAddress3;
        this.rAddress4 = rAddress4;
        this.rPostCode = rPostCode;
        this.rIsoCountry = rIsoCountry;
        this.clientClass = clientClass;
        this.clientType = clientType;
        this.greeting = greeting;
    }

    @JsonProperty(value="AccountID")
    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
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

    @JsonProperty(value="DateCreated")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @JsonProperty(value="BranchCode")
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    @JsonProperty(value="BranchName")
    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @JsonProperty(value="AdviserCode")
    public String getAdviserCode() {
        return adviserCode;
    }

    public void setAdviserCode(String adviserCode) {
        this.adviserCode = adviserCode;
    }

    @JsonProperty(value="AdviserName")
    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
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

    @JsonProperty(value="Address1")
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @JsonProperty(value="Address2")
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @JsonProperty(value="Address3")
    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    @JsonProperty(value="Address4")
    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    @JsonProperty(value="Address5")
    public String getAddress5() {
        return address5;
    }

    public void setAddress5(String address5) {
        this.address5 = address5;
    }

    @JsonProperty(value="Postcode")
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
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

    @JsonProperty(value="RName1")
    public String getrName1() {
        return rName1;
    }

    public void setrName1(String rName1) {
        this.rName1 = rName1;
    }

    @JsonProperty(value="RName2")
    public String getrName2() {
        return rName2;
    }

    public void setrName2(String rName2) {
        this.rName2 = rName2;
    }

    @JsonProperty(value="RName3")
    public String getrName3() {
        return rName3;
    }

    public void setrName3(String rName3) {
        this.rName3 = rName3;
    }

    @JsonProperty(value="Designation")
    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @JsonProperty(value="RAddress1")
    public String getrAddress1() {
        return rAddress1;
    }

    public void setrAddress1(String rAddress1) {
        this.rAddress1 = rAddress1;
    }

    @JsonProperty(value="RAddress2")
    public String getrAddress2() {
        return rAddress2;
    }

    public void setrAddress2(String rAddress2) {
        this.rAddress2 = rAddress2;
    }

    @JsonProperty(value="RAddress3")
    public String getrAddress3() {
        return rAddress3;
    }

    public void setrAddress3(String rAddress3) {
        this.rAddress3 = rAddress3;
    }

    @JsonProperty(value="RAddress4")
    public String getrAddress4() {
        return rAddress4;
    }

    public void setrAddress4(String rAddress4) {
        this.rAddress4 = rAddress4;
    }

    @JsonProperty(value="RPostCode")
    public String getrPostCode() {
        return rPostCode;
    }

    public void setrPostCode(String rPostCode) {
        this.rPostCode = rPostCode;
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

    @JsonProperty(value="Greeting")
    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
