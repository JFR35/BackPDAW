package com.myobservation.ehr.config;

import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClient;
import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClientConfig;
import org.ehrbase.openehr.sdk.client.openehrclient.defaultrestclient.DefaultRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class EhrbaseConfig {

    @Bean
    public OpenEhrClient openEhrClient() {
        OpenEhrClientConfig config = new OpenEhrClientConfig(
                URI.create("http://localhost:8089/ehrbase/rest/openehr/v1")
                // Uncomment if authentication is required
                // , "ehrbase-user", "SuperSecretPassword"
        );
        return new DefaultRestClient(config);
    }
}