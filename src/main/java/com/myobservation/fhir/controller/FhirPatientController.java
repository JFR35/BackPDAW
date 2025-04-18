package com.myobservation.fhir.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.fhir.persistence.FhirPatientRepository;
import com.myobservation.fhir.fhir.FhirPatientEntity;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
public class FhirPatientController {

    private static final Logger log = LoggerFactory.getLogger(FhirPatientController.class);

    private final FhirContext fhirContext;
    private final FhirValidator fhirValidator;
    private final FhirPatientRepository fhirPatientRepository;
    private final IParser jsonParser; // Para serializar/deserializar en JSON

    public FhirPatientController(FhirContext fhirContext, FhirValidator fhirValidator,
                                 FhirPatientRepository fhirPatientRepository) {
        this.fhirContext = fhirContext;
        this.fhirValidator = fhirValidator;
        this.fhirPatientRepository = fhirPatientRepository;
        this.jsonParser = fhirContext.newJsonParser().setPrettyPrint(true);
    }

    @PostMapping
    public ResponseEntity<String> createPatient(@RequestBody String patientJson) {
        log.info("Received raw patient JSON: [{}]", patientJson);
        try {
            // Validar y convertir el JSON antes de insertarlo
            if (patientJson == null || patientJson.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty or null JSON");
            }

            // Aquí puedes hacer una validación extra si lo necesitas
            Patient patient = jsonParser.parseResource(Patient.class, patientJson);
            log.info("Parsed patient resource: {}", patient.getId());

            // Validar el recurso FHIR antes de guardarlo
            ValidationResult result = fhirValidator.validateWithResult(patient);
            if (result.isSuccessful()) {
                FhirPatientEntity entity = new FhirPatientEntity();
                entity.setResourcePatientJson(patientJson);  // Guardar el JSON como String
                fhirPatientRepository.save(entity);
                return ResponseEntity.status(HttpStatus.CREATED).body("Patient created successfully");
            } else {
                String errorDetails = result.getMessages().stream()
                        .map(SingleValidationMessage::getMessage)
                        .collect(Collectors.joining("\n"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid FHIR resource:\n" + errorDetails);
            }
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<FhirPatientEntity>> getAllPatients() {
        List<FhirPatientEntity> patients = fhirPatientRepository.findAll();
        return ResponseEntity.ok(patients);
    }



    @GetMapping("/{id}")
    public ResponseEntity<String> getPatientById(@PathVariable Long id) {
        Optional<FhirPatientEntity> entity = fhirPatientRepository.findById(id);
        return entity.map(e -> ResponseEntity.ok(e.getResourcePatientJson()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePatient(@PathVariable Long id, @RequestBody String patientJson) {
        try {
            Patient patient = jsonParser.parseResource(Patient.class, patientJson);
            ValidationResult result = fhirValidator.validateWithResult(patient);
            if (!result.isSuccessful()) {
                String errors = result.getMessages().stream()
                        .map(SingleValidationMessage::getMessage)
                        .collect(Collectors.joining("\n"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid FHIR resource:\n" + errors);
            }
            Optional<FhirPatientEntity> entityOpt = fhirPatientRepository.findById(id);
            if (entityOpt.isPresent()) {
                FhirPatientEntity entity = entityOpt.get();
                entity.setResourcePatientJson(patientJson);
                fhirPatientRepository.save(entity);
                return ResponseEntity.ok("Patient updated successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        } catch (Exception e) {
            log.error("Error updating patient: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable Long id) {
        try {
            if (fhirPatientRepository.existsById(id)) {
                fhirPatientRepository.deleteById(id);
                return ResponseEntity.ok("Patient deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        } catch (Exception e) {
            log.error("Error deleting patient: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }

    }
}