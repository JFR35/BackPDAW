package com.myobservation.ehr.config;

import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClient;
import org.ehrbase.openehr.sdk.client.openehrclient.defaultrestclient.DefaultRestClient;
import org.ehrbase.openehr.sdk.client.templateprovider.ClientTemplateProvider;
import org.ehrbase.openehr.sdk.response.dto.TemplateResponseData;
import org.ehrbase.openehr.sdk.webtemplate.templateprovider.TemplateProvider;
import org.openehr.schemas.v1.OPERATIONALTEMPLATE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configuration
public class TemplateSetup implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(TemplateSetup.class);

    @Autowired
    private OpenEhrClient openEhrClient;

    @Override
    public void run(String... args) throws Exception {
        String templateId = "Presión Sanguínea";
        String templateFilename = "Presion_Sanguinea.opt";

        // Check if template exists using the TemplateProvider
        TemplateProvider templateProvider = new ClientTemplateProvider((DefaultRestClient) openEhrClient);
        try {
            Optional<OPERATIONALTEMPLATE> template = templateProvider.find(templateId);
            if (template.isPresent()) {
                logger.info("Template already exists: {}", templateId);
                return;
            }
        } catch (Exception e) {
            logger.info("Template not found, will upload: {}", templateId);
        }

        // Upload template if it doesn't exist
        logger.info("Uploading template: {}", templateId);
        File optFile = new File("src/main/resources/Presion_Sanguinea.opt");
        if (!optFile.exists()) {
            logger.error("Template file not found: {}", optFile.getPath());
            throw new RuntimeException("Template file not found: " + optFile.getPath());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        // Uncomment if authentication is required
        // headers.setBasicAuth("ehrbase-user", "SuperSecretPassword");
        String optContent = new String(Files.readAllBytes(optFile.toPath()));
        HttpEntity<String> request = new HttpEntity<>(optContent, headers);

        String url = "http://localhost:8089/ehrbase/rest/openehr/v1/definition/template";
        restTemplate.postForEntity(url, request, String.class);
        logger.info("Template uploaded successfully: {}", templateId);
    }
}