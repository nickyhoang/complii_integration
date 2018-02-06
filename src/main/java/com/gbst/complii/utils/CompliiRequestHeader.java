package com.gbst.complii.utils;

/**
 * The request header which contains the authentication data for web service request to Complii
 */
public class CompliiRequestHeader {

    private String licensee;
    private String licenseeToken;
    private String authorization;

    public CompliiRequestHeader() {
    }

    public CompliiRequestHeader(String licensee, String licenseeToken, String authorization) {
        this.licensee = licensee;
        this.licenseeToken = licenseeToken;
        this.authorization = authorization;
    }

    public String getLicensee() {
        return licensee;
    }

    public void setLicensee(String licensee) {
        this.licensee = licensee;
    }

    public String getLicenseeToken() {
        return licenseeToken;
    }

    public void setLicenseeToken(String licenseeToken) {
        this.licenseeToken = licenseeToken;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
