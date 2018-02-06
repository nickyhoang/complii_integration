package com.gbst.complii.account;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Post Order result returned from Complii
 */
public class AccountPostResult {

    private Boolean success;
    private String message;
    private Integer identifier;
    private String accountNumber;

    public AccountPostResult() {
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

    @Override
    public String toString() {
        return "AccountPostResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", identifier='" + identifier + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
