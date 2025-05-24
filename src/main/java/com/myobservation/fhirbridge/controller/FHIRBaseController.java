package com.myobservation.fhirbridge.controller;

import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.fhirbridge.common.FHIRConstants;
import com.myobservation.fhirbridge.service.FHIRBaseService;
import com.myobservation.fhirbridge.service.FHIRValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
            return ResponseEntity.badRequest().body("Error: Resultado de validación es nulo");
        }
        if (!result.isSuccessful()) {
            logger.warn("Validación fallida para Patient");
            return ResponseEntity.badRequest().body(result.getMessages());
        }
        try {
            String response = fhirBaseService.storeResource(FHIRConstants.PATIENT_RESOURCE_TYPE, jsonPatient);

            logger.info("Paciente almacenado exitosamente");
            return ResponseEntity.ok("Paciente almacenado: " + response);
        } catch (Exception e) {
            logger.error("Error al almacenar Patient", e);
            return ResponseEntity.internalServerError().body("Error al almacenar Patient: " + e.getMessage());
        }
    }

    @PostMapping("/Practitioner")
    public ResponseEntity<?> storePractitioner(@RequestBody String jsonPractitioner) {
        logger.info("Recibida solicitud para almacenar Practitioner");
        ValidationResult result = validationService.validatePractitioner(jsonPractitioner);
        if (result == null) {
            logger.error("Error: Resultado de validación es null");
            return ResponseEntity.badRequest().body("Error: Resultado de validación es nulo");
        }
        if (!result.isSuccessful()) {
            logger.warn("Validación fallida para Practitioner");
            return ResponseEntity.badRequest().body(result.getMessages());
        }
        try {
            String response = fhirBaseService.storeResource(FHIRConstants.PRACTITIONER_RESOURCE_TYPE, jsonPractitioner);
            logger.info("Practitioner almacenado exitosamente");
            return ResponseEntity.ok("Practitioner almacenado: " + response);
        } catch (Exception e) {
            logger.error("Error al almacenar Practitioner", e);
            return ResponseEntity.internalServerError().body("Error al almacenar Practitioner: " + e.getMessage());
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

    @GetMapping("/{resourceType}")
    public ResponseEntity<?> getAllResources(@PathVariable String resourceType) {
        logger.info("Solicitud para obtener todos los recursos del tipo {}", resourceType);
        try {
            String response = fhirBaseService.getAllResources(resourceType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener recursos {}", resourceType, e);
            return ResponseEntity.internalServerError().body("Error al obtener recursos: " + e.getMessage());
        }
    }

    @GetMapping("/{resourceType}/{id}")
    public ResponseEntity<?> getResourceById(@PathVariable String resourceType, @PathVariable String id) {
        logger.info("Solicitud para obtener recurso {}/{}", resourceType, id);

        try {
            Optional<String> resourceJson = fhirBaseService.getResourceById(resourceType, id);

            return resourceJson
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Recurso " + resourceType + "/" + id + " no encontrado"));
        } catch (Exception e) {
            logger.error("Error al obtener recurso {}/{}: {}", resourceType, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener recurso: " + e.getMessage());
        }
    }
}