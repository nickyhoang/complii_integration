package com.gbst.complii.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigurationServiceImpl implements ConfigurationService {

    private NamedParameterJdbcTemplate frontOfficeTemplate;

    private String getQuery = "select config_value from configuration where config_key = :configKey";

    private String setQuery = "update configuration set config_value = :configValue WHERE CONFIG_KEY = :configKey";

    @Override
    public <T> T getValue(ConfigurationKey key) {
        Map<String, String> params = new HashMap<>();
        params.put("configKey", key.name());
        String configValue = frontOfficeTemplate.queryForObject(getQuery,params,String.class);
        return getConfigurationValueType(key,configValue);
    }

    @Override
    public <T> void setValue(ConfigurationKey key, T value) {
        Map<String, String> params = new HashMap<>();
        params.put("configKey", key.name());
        params.put("configValue", String.valueOf(value));
        frontOfficeTemplate.update(setQuery, params);
    }

    private <T> T getConfigurationValueType(ConfigurationKey key, String configurationValue) {
        if (configurationValue == null) {
            return null;
        }
        Class requiredType = key.getRequiredType();
        if (String.class.equals(requiredType) || configurationValue == null) {
            return (T) configurationValue;
        } else if(Integer.class.equals(requiredType)) {
            return (T) Integer.valueOf(configurationValue);
        } else if(Long.class.equals(requiredType)) {
            return (T) Long.valueOf(configurationValue);
        } else if(Boolean.class.equals(requiredType)) {
            return (T) Boolean.valueOf(configurationValue);
        }
        throw new RuntimeException("Unsupported class: " + requiredType);
    }

    @Autowired
    public void setFrontOfficeDataSource(DataSource frontOfficeDataSource) {
        this.frontOfficeTemplate = new NamedParameterJdbcTemplate(frontOfficeDataSource);
    }
}
