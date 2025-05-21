package com.myobservation.fhir.controller;

import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.fhir.service.FHIRBaseService;
import com.myobservation.fhir.service.FHIRValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fhir")
public class FhirController {
    private final FHIRValidationService validationService;
    private final FHIRBaseService fhirBaseService;

    public FhirController(FHIRValidationService validationService, FHIRBaseService fhirBaseService) {
        this.validationService = validationService;
        this.fhirBaseService = fhirBaseService;
    }

    @PostMapping("/validate-patient")
    public ResponseEntity<String> validateAndStorePatient(@RequestBody String patientJson) {
        ValidationResult result = validationService.validatePatient(patientJson);
        if (result == null || !result.isSuccessful()) {
            return ResponseEntity.badRequest().body("Error en la validación de Patient: " + result.getMessages());
        }
        try {
            String response = fhirBaseService.storePatient(patientJson);
            return ResponseEntity.ok("Patient almacenado: " + response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al almacenar Patient: " + e.getMessage());
        }
    }

    @PostMapping("/validate-practitioner")
    public ResponseEntity<String> validateAndStorePractitioner(@RequestBody String practitionerJson) {
        ValidationResult result = validationService.validatePractitioner(practitionerJson);
        if (result == null || !result.isSuccessful()) {
            return ResponseEntity.badRequest().body("Error en la validación de Practitioner: " + result.getMessages());
        }
        try {
            String response = fhirBaseService.storeResource("Practitioner", practitionerJson);
            return ResponseEntity.ok("Practitioner almacenado: " + response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al almacenar Practitioner: " + e.getMessage());
        }
    }
}