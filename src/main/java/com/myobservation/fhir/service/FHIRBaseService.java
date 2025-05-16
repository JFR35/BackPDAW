package com.myobservation.fhir.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class FHIRBaseService {

    private final RestTemplate restTemplate;

    @Value("${fhirbase.url}")
    private String fhirBaseUrl;

    @Value("${aidbox.username:root}")
    private String aidboxUsername;

    @Value("${aidbox.password:_rC3KbnOvW}")
    private String aidboxPassword;

    public FHIRBaseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String storeResource(String jsonPatient) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/fhir+json"));

            // Configurar autenticación básica
            String authString = aidboxUsername + ":" + aidboxPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            HttpEntity<String> request = new HttpEntity<>(jsonPatient, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    fhirBaseUrl + "/Patient",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("Error en storeResource: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        }
    }
}