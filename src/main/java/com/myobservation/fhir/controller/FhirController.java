package com.myobservation.fhir.controller;

import com.myobservation.fhir.service.FHIRValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Clase para validad locamente un Recurso Patient contra mi
 * StructureDefinition con HAPI FHIR, sin almacenar nada y sin
 * conectar a FHIRBase con contenedor Docker
 */
@RestController
@RequestMapping("/api/fhir")
public class FhirController {

    private final FHIRValidationService validationService;
    private final RestTemplate restTemplate;

    public FhirController(FHIRValidationService validationService, RestTemplate restTemplate) {
        this.validationService = validationService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/validate-patient")
    public ResponseEntity<String> validateAndStore(@RequestBody String patientJson) {
        boolean isValid = validationService.validatePatient(patientJson).isSuccessful();

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en validación FHIR.");
        }

        // Enviar el paciente validado a FHIRBase
        String fhirbaseUrl = "http://fhirbase:3000/fhir/Patient";
        ResponseEntity<String> response = restTemplate.postForEntity(fhirbaseUrl, patientJson, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
