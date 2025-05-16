package com.myobservation.fhir.controller;

import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.fhir.service.FHIRBaseService;
import com.myobservation.fhir.service.FHIRValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fhir")
public class FHIRBaseController {

    private static final Logger logger = LoggerFactory.getLogger(FHIRBaseController.class);

    private final FHIRValidationService validationService;
    private final FHIRBaseService fhirBaseService;

    public FHIRBaseController(FHIRValidationService validationService, FHIRBaseService fhirBaseService) {
        this.validationService = validationService;
        this.fhirBaseService = fhirBaseService;
    }

    /**
     * Endpoint para almacenar pacientes
     *
     * @param jsonPatient JSON del paciente a almacenar
     * @return Respuesta con resultado de la operación
     */
    @PostMapping("/Patient")
    public ResponseEntity<?> storePatient(@RequestBody String jsonPatient) {
        logger.info("Recibida solicitud para almacenar Patient");

        ValidationResult result = validationService.validatePatient(jsonPatient);

        if (result == null) {
            logger.error("Error: Resultado de validación es null");
            return ResponseEntity.badRequest().body("Error: Resultado de validación es null");
        }

        if (!result.isSuccessful()) {
            logger.warn("Validación fallida para Patient");
            return ResponseEntity.badRequest().body(result.getMessages());
        }

        try {
            String response = fhirBaseService.storePatient(jsonPatient);
            logger.info("Patient almacenado exitosamente");
            return ResponseEntity.ok("Patient almacenado: " + response);
        } catch (Exception e) {
            logger.error("Error al almacenar Patient", e);
            return ResponseEntity.internalServerError().body("Error al almacenar Patient: " + e.getMessage());
        }
    }

    /**
     * Endpoint genérico para almacenar recursos FHIR
     *
     * @param resourceType Tipo de recurso FHIR
     * @param jsonResource JSON del recurso a almacenar
     * @return Respuesta con resultado de la operación
     */
    @PostMapping("/{resourceType}")
    public ResponseEntity<?> storeResource(
            @PathVariable String resourceType,
            @RequestBody String jsonResource) {

        logger.info("Recibida solicitud para almacenar recurso de tipo {}", resourceType);

        // Aquí podría añadirse validación específica según el tipo de recurso

        try {
            String response = fhirBaseService.storeResource(resourceType, jsonResource);
            logger.info("Recurso {} almacenado exitosamente", resourceType);
            return ResponseEntity.ok(resourceType + " almacenado: " + response);
        } catch (Exception e) {
            logger.error("Error al almacenar recurso {}", resourceType, e);
            return ResponseEntity.internalServerError()
                    .body("Error al almacenar " + resourceType + ": " + e.getMessage());
        }
    }
}