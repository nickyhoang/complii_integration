package com.gbst.complii;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;

@Component
@Configurable
public class RestClientConfiguration {

    private Logger logger = LoggerFactory.getLogger(RestClientConfiguration.class);

    @Bean
    public ClientConfig clientConfig() {
        SSLContext sslContext = null;

        try {

            KeyManager[] keyManagers = null;
            TrustManager[] trustManagers = null;
            String passwordDecrypt = "password";

            InputStream keyStoreInputStream = RestClientConfiguration.class.getClassLoader().getResourceAsStream("complii-keys.jks");
            if (keyStoreInputStream == null) {
                keyStoreInputStream = RestClientConfiguration.class.getClassLoader().getResourceAsStream("default-complii-keys.jks");
            }

            if (keyStoreInputStream != null) {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(keyStoreInputStream, passwordDecrypt.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(keyStore, passwordDecrypt.toCharArray());

                keyStoreInputStream.close();
                keyManagers = kmf.getKeyManagers();
            }

            InputStream trustStoreInputStream = RestClientConfiguration.class.getClassLoader().getResourceAsStream("complii-trust.jks");
            if (trustStoreInputStream == null) {
                trustStoreInputStream = RestClientConfiguration.class.getClassLoader().getResourceAsStream("default-complii-trust.jks");
            }

            if (trustStoreInputStream != null) {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(trustStoreInputStream, passwordDecrypt.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);

                trustManagers = tmf.getTrustManagers();
            }

            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagers, trustManagers, null);
        } catch (Exception e) {
            logger.error("Unable to setup SSL Configuration for Complii connections.  Default SSL configuration will be used", e);
        }

        DefaultClientConfig clientConfig = new DefaultClientConfig(JacksonJsonProvider.class);
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        if (sslContext != null) {
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(null, sslContext));
        }

        return clientConfig;
    }
}
