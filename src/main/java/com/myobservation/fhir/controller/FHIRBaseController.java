package com.myobservation.fhir.controller;

import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.fhir.service.FHIRBaseService;
import com.myobservation.fhir.service.FHIRValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.fhir.service.FHIRBaseService;
import com.myobservation.fhir.service.FHIRValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FHIRBaseController {

    private final FHIRValidationService validationService;
    private final FHIRBaseService fhirBaseService;

    public FHIRBaseController(FHIRValidationService validationService, FHIRBaseService fhirBaseService) {
        this.validationService = validationService;
        this.fhirBaseService = fhirBaseService;
    }

    @PostMapping("/Patient")
    public ResponseEntity<?> storePatient(@RequestBody String jsonPatient) {
        ValidationResult result = validationService.validatePatient(jsonPatient);

        if (result == null) {
            return ResponseEntity.badRequest().body("Error: Resultado de validación es null");
        }
        if (!result.isSuccessful()) {
            return ResponseEntity.badRequest().body(result.getMessages());
        }

        String response = fhirBaseService.storeResource(jsonPatient);
        return ResponseEntity.ok("Patient almacenado: " + response);
    }
}