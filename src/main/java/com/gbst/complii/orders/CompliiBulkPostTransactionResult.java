package com.gbst.complii.orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * The Bulk Post transaction response
 */
public class CompliiBulkPostTransactionResult {

    private Boolean success;
    private String message;
    private ArrayList<OrderPostResult> postedOrders;

    public CompliiBulkPostTransactionResult() {
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

    @JsonProperty(value="PostedOrders")
    public ArrayList<OrderPostResult> getPostedOrders() {
        return postedOrders;
    }

    public void setPostedOrders(ArrayList<OrderPostResult> postedOrders) {
        this.postedOrders = postedOrders;
    }

    @Override
    public String toString() {
        return "CompliiBulkPostTransactionResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", postedOrders=" + postedOrders +
                '}';
    }
}
