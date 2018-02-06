package com.gbst.complii.orders;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Post Order result returned from Complii
 */

public class OrderPostResult {

    private boolean success;
    private String message;
    private int identifier;
    private String orderNumber;

    public OrderPostResult() {
    }

    @JsonProperty(value="Success")
    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
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
    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @JsonProperty(value="OrderNumber")
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "OrderPostResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", identifer='" + identifier + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                '}';
    }
}
