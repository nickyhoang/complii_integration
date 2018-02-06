package com.gbst.complii.dataMapper;

import java.util.Map;

public class FoCompliiDataMapper  {

    private Map<String, String> orderMappingMap;
    private Map<String, String> accountMappingMap;
    private Map<String, String> holdingMappingMap;

    public void setOrderMappingMap(Map<String, String> orderMappingMap) {
        this.orderMappingMap = orderMappingMap;
    }

    public void setAccountMappingMap(Map<String, String> accountMappingMap) {
        this.accountMappingMap = accountMappingMap;
    }

    public void setHoldingMappingMap(Map<String, String> holdingMappingMap) {
        this.holdingMappingMap = holdingMappingMap;
    }

    public Map<String, String> getOrderMappingMap() {
        return orderMappingMap;
    }

    public Map<String, String> getAccountMappingMap() {
        return accountMappingMap;
    }

    public Map<String, String> getHoldingMappingMap() {
        return holdingMappingMap;
    }
}
