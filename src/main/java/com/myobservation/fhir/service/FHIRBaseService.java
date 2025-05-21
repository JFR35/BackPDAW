package com.myobservation.fhir.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;



import java.util.Base64;

/**
 * Servicio base para interactuar con el servidor FHIR de AidBox
 */
@Service
public class FHIRBaseService {

    private static final Logger logger = LoggerFactory.getLogger(FHIRBaseService.class);
    private static final String CONTENT_TYPE = "application/fhir+json";

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

    /**
     * Almacena un structureDefinition FHIR en el servidor AidBox
     * @param resourceType Tipo de recurso FHIR (ej: Patient, Practitioner...)
     * @param jsonResource JSON del recurso a almacenar
     * @return Respuesta del servidor
     */
    public String storeResource(String resourceType, String jsonResource) {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();

            HttpEntity<String> request = new HttpEntity<>(jsonResource, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    fhirBaseUrl + "/" + resourceType,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            logger.info("Recurso {} Almacenado exitosamente: ", resourceType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.info("Error al almacenar recurso {}: {} - {}",
                    resourceType, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al almacenar el recurso: " + resourceType, e);
        }
    }

    /**
     * Método auxiliar para crear cabeceras HTTP con autenticación
     * @return Cabeceras HTTP configuradas
     */
    private HttpHeaders createAuthenticatedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(CONTENT_TYPE));

        // Configurar autenticación básica
        String authString = aidboxUsername + ":" + aidboxPassword;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        return headers;
    }

    /**
     * Almacena un recurso de tipo Patient
     * @param jsonPatient JSON del recurso Patient
     * @return Respuesta del servidor
     */
    public String storePatient(String jsonPatient) {
        return storeResource("Patient", jsonPatient);
    }
}