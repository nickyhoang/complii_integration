package com.gbst.complii.holding;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * The Bulk Post Holding response
 */
public class BulkHoldingPostResult {


    private Boolean success;
    private String message;
    private ArrayList<HoldingPostResult> postedHoldings;

    public BulkHoldingPostResult() {
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

    @JsonProperty(value="PostedHoldings")
    public ArrayList<HoldingPostResult> getPostedHoldings() {
        return postedHoldings;
    }

    public void setPostedHoldings(ArrayList<HoldingPostResult> postedHoldings) {
        this.postedHoldings = postedHoldings;
    }

    @Override
    public String toString() {
        return "BulkHoldingPostResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", postedHoldings=" + postedHoldings +
                '}';
    }
}
