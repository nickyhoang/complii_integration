package com.gbst.complii.configuration;

public interface ConfigurationService {
    <T> T getValue(ConfigurationKey key);
    <T> void setValue(ConfigurationKey key, T value);
}
