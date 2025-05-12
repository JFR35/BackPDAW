package com.myobservation.fhir.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FHIRBaseService {

    private final RestTemplate restTemplate;

    @Value("${fhirbase.url}")
    private String fhirBaseUrl;

    public FHIRBaseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String storeResource(String jsonPatient) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonPatient, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                fhirBaseUrl + "/Patient",
                HttpMethod.POST,
                request,
                String.class
        );

        return response.getBody();
    }
}
