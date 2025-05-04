package com.myobservation.fhir.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.parser.IParser;

@Service
public class FHIRValidationService {

    private static final Logger logger = LoggerFactory.getLogger(FHIRValidationService.class);

    private final FhirValidator fhirValidator;
    private final FhirContext fhirContext;
    private final IParser jsonParser;

    @Autowired
    public FHIRValidationService(FhirValidator fhirValidator, FhirContext fhirContext) {
        this.fhirValidator = fhirValidator;
        this.fhirContext = fhirContext;
        this.jsonParser = fhirContext.newJsonParser();
    }

    public ValidationResult validatePatient(String jsonPatient) {
        try {
            Patient patient = jsonParser.parseResource(Patient.class, jsonPatient);

            if (fhirContext.getValidationSupport().fetchStructureDefinition("http://hl7.org/fhir/us/example/StructureDefinition/mi-paciente-persistencia") == null) {
                logger.error("❌ Error: `StructureDefinition` no encontrado en HAPI FHIR.");
                return null;
            }

            ValidationResult result = fhirValidator.validateWithResult(patient);

            logValidationResults(result);
            return result;
        } catch (Exception e) {
            logger.error("Error al validar el Patient", e);
            throw new RuntimeException("Error al validar el Patient", e);
        }
    }
    public void checkStructureDefinition() {
        StructureDefinition structureDefinition = (StructureDefinition) fhirContext.getValidationSupport()
                .fetchStructureDefinition("http://hl7.org/fhir/us/example/StructureDefinition/mi-paciente-persistencia");

        if (structureDefinition == null) {
            System.out.println("❌ Error: `StructureDefinition` no encontrado en HAPI FHIR.");
        } else {
            System.out.println("✅ `StructureDefinition` cargado correctamente en HAPI FHIR.");
        }
    }


    private void logValidationResults(ValidationResult result) {
        if (result.isSuccessful()) {
            logger.info("✅ Validación exitosa");
        } else {
            logger.warn("❌ La validación ha fallado con {} errores/advertencias", result.getMessages().size());

            result.getMessages().forEach(message -> {
                if (message.getSeverity() == ResultSeverityEnum.ERROR) {
                    logger.error("❌ ERROR: {} - En ubicación: {}", message.getMessage(), message.getLocationString());
                } else if (message.getSeverity() == ResultSeverityEnum.WARNING) {
                    logger.warn("⚠️ ADVERTENCIA: {} - En ubicación: {}", message.getMessage(), message.getLocationString());
                } else {
                    logger.info("ℹ️ INFO: {} - En ubicación: {}", message.getMessage(), message.getLocationString());
                }
            });
        }
    }
}
