package com.myobservation.fhir.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona funcionalidades de validación para recursos FHIR
 */
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

    /**
     * Valida un recurso Patient contra el perfil configurado
     * @param jsonPatient JSON del paciente a validar
     * @return Resultado de la validación
     */
    public ValidationResult validatePatient(String jsonPatient) {
        return validateResource(jsonPatient, Patient.class, "http://hl7.org/fhir/us/example/StructureDefinition/mi-paciente-persistencia");
    }

    /**
     * Método genérico para validar cualquier tipo de recurso FHIR
     * @param <T> Tipo de recurso FHIR
     * @param jsonResource JSON del recurso a validar
     * @param resourceClass Clase del recurso
     * @param profileUrl URL del perfil contra el que validar
     * @return Resultado de la validación
     */
    public <T extends Resource> ValidationResult validateResource(String jsonResource, Class<T> resourceClass, String profileUrl) {
        try {
            T resource = jsonParser.parseResource(resourceClass, jsonResource);

            // Verificar que el perfil existe
            if (fhirContext.getValidationSupport().fetchStructureDefinition(profileUrl) == null) {
                logger.error("Error: StructureDefinition '{}' no encontrado en HAPI FHIR", profileUrl);
                throw new RuntimeException("Perfil de validación no encontrado: " + profileUrl);
            }

            ValidationResult result = fhirValidator.validateWithResult(resource);
            logValidationResults(result);
            return result;
        } catch (Exception e) {
            logger.error("Error al validar el recurso {}", resourceClass.getSimpleName(), e);
            throw new RuntimeException("Error al validar el recurso " + resourceClass.getSimpleName(), e);
        }
    }

    /**
     * Verifica la disponibilidad de un StructureDefinition en el contexto
     * @param profileUrl URL del perfil a verificar
     * @return true si el perfil está cargado, false en caso contrario
     */
    public boolean isStructureDefinitionAvailable(String profileUrl) {
        StructureDefinition structureDefinition = (StructureDefinition) fhirContext.getValidationSupport()
                .fetchStructureDefinition(profileUrl);

        if (structureDefinition == null) {
            logger.error("Error: StructureDefinition '{}' no encontrado en HAPI FHIR", profileUrl);
            return false;
        } else {
            logger.info("StructureDefinition '{}' cargado correctamente en HAPI FHIR", profileUrl);
            return true;
        }
    }

    /**
     * Registra los resultados de la validación en el log
     * @param result Resultado de la validación
     */
    private void logValidationResults(ValidationResult result) {
        if (result.isSuccessful()) {
            logger.info("Validación exitosa");
        } else {
            logger.warn("La validación ha fallado con {} errores/advertencias", result.getMessages().size());

            result.getMessages().forEach(message -> {
                switch (message.getSeverity()) {
                    case ERROR:
                        logger.error("ERROR: {} - En ubicación: {}", message.getMessage(), message.getLocationString());
                        break;
                    case WARNING:
                        logger.warn("ADVERTENCIA: {} - En ubicación: {}", message.getMessage(), message.getLocationString());
                        break;
                    default:
                        logger.info("INFO: {} - En ubicación: {}", message.getMessage(), message.getLocationString());
                        break;
                }
            });
        }
    }
}