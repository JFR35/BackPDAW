package com.myobservation.fhir.controller;


import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.fhir.service.FHIRValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fhir")
public class FHIRValidationController {
    private final FHIRValidationService validationService;

    public FHIRValidationController(FHIRValidationService validationService) {
        this.validationService = validationService;
    }
    @PostMapping("/validate")
    public ResponseEntity<?> validatePatient(@RequestBody String jsonPatient) {
        ValidationResult result = validationService.validatePatient(jsonPatient);

        if (result == null || !result.isSuccessful()) {
            return ResponseEntity.badRequest().body(result.getMessages());
        }

        return ResponseEntity.ok("Patient validado con éxito");
    }

}
