package com.gbst.complii.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * The Bulk Post Account response
 */
public class BulkAccountPostResult {

    private Boolean success;
    private String message;
    private ArrayList<AccountPostResult> postedAccounts;

    public BulkAccountPostResult() {
    }

    public BulkAccountPostResult(Boolean success, String message, ArrayList<AccountPostResult> postedAccounts) {
        this.success = success;
        this.message = message;
        this.postedAccounts = postedAccounts;
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

    @JsonProperty(value="PostedAccounts")
    public ArrayList<AccountPostResult> getPostedAccounts() {
        return postedAccounts;
    }

    public void setPostedAccounts(ArrayList<AccountPostResult> postedAccounts) {
        this.postedAccounts = postedAccounts;
    }

    @Override
    public String toString() {
        return "BulkAccountPostResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", postedAccounts=" + postedAccounts +
                '}';
    }
}
