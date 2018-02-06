package com.gbst.complii.holding;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Post Holding result returned from Complii
 */
public class HoldingPostResult {

    private Boolean success;
    private String message;
    private Integer identifier;
    private String accountNumber;
    private String securityCode;

    public HoldingPostResult() {
    }

    @JsonProperty(value="Success")
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonProperty(value="Message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty(value="Identifier")
    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    @JsonProperty(value="AccountNumber")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @JsonProperty(value="SecurityCode")
    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    @Override
    public String toString() {
        return "HoldingPostResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", identifier=" + identifier +
                ", accountNumber='" + accountNumber + '\'' +
                ", securityCode='" + securityCode + '\'' +
                '}';
    }
}
