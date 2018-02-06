package com.gbst.complii.configuration;

public enum ConfigurationKey {
    COMPLII_URL(String.class),
    COMPLII_LICENSEE_TOKEN(String.class),
    COMPLII_APPKEY(String.class),
    COMPLII_AUTHORIZATION(String.class),
    COMPLII_INTERNAL_POST_ORDER_RESOURCE(String.class),
    COMPLII_POST_ORDER_RESOURCE(String.class),
    COMPLII_BULK_POST_ORDER_RESOURCE(String.class),
    COMPLII_BULK_POST_ORDER_TRANSACTION_RESOURCE(String.class),
    COMPLII_SEARCH_ORDER_RESOURCE(String.class),
    COMPLII_POST_ACCOUNT_RESOURCE(String.class),
    COMPLII_BULK_POST_ACCOUNT_RESOURCE(String.class),
    COMPLII_SEARCH_ACCOUNT_RESOURCE(String.class),
    COMPLII_POST_HOLDING_RESOURCE(String.class),
    COMPLII_BULK_POST_HOLDING_RESOURCE(String.class),
    COMPLII_SEARCH_HOLDING_RESOURCE(String.class),
    COMPLII_ORDER_SERVICE_ENABLED(Boolean.class),
    COMPLII_ACCOUNT_SERVICE_ENABLED(Boolean.class),
    COMPLII_HOLDING_SERVICE_ENABLED(Boolean.class),
    COMPLII_ORDER_LAST_SUCCESSFUL_PUSH(String.class),
    COMPLII_ACCOUNT_LAST_SUCCESSFUL_PUSH(String.class),
    COMPLII_HOLDING_LAST_SUCCESSFUL_PUSH(String.class),
    COMPLII_ORDER_POST_STRATEGY(String.class),
    COMPLII_ACCOUNT_POST_STRATEGY(String.class),
    COMPLII_HOLDING_POST_STRATEGY(String.class),
    COMPLII_ORDER_SQL_FILE(String.class),
    COMPLII_ACCOUNT_SQL_FILE(String.class),
    COMPLII_HOLDING_SQL_FILE(String.class),
    COMPLII_TEST_MODE(Boolean.class),
    COMPLII_LIMIT_OF_NUMBER_OF_ORDER_RECORD_IN_BULK_REQUEST(String.class),
    COMPLII_LIMIT_OF_NUMBER_OF_ACCOUNT_RECORD_IN_BULK_REQUEST(String.class),
    COMPLII_LIMIT_OF_NUMBER_OF_HOLDING_RECORD_IN_BULK_REQUEST(String.class);

    private final Class<?> requiredType;

    private <T> ConfigurationKey(Class<T> requiredType) {
        this.requiredType = requiredType;
    }

    public Class getRequiredType() {
        return requiredType;
    }
}
