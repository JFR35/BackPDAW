package com.myobservation.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Clase de configuración para la validación de recursos FHIR en el sistema.
 * Las validaciones se realizan contra el StructureDefinition en una implementación local.
 */
@Configuration
public class FhirValidationConfig {

    private static final Logger log = LoggerFactory.getLogger(FhirValidationConfig.class);

    /**
     * Crea el contexto de FHIR en versión R4
     * @return Instancia de {@link FhirContext}
     */
    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    /**
     * Carga el perfil personalizado desde un archivo JSON.
     * @param fhirContext Contexto de FHIR usado en la validación.
     * @param resourceLoader Carga el recurso desde el classpath.
     * @return Instancia de {@link PrePopulatedValidationSupport} con el perfil cargado.
     * @throws IOException Si no se encuentra el perfil o hay errores en la carga.
     */
    @Bean
    public PrePopulatedValidationSupport prePopulatedValidationSupport(FhirContext fhirContext, ResourceLoader resourceLoader) throws IOException {
        PrePopulatedValidationSupport support = new PrePopulatedValidationSupport(fhirContext);
        String resourcePath = "classpath:/fhir-profiles/StructureDefinition-mi-practitioner-persistencia.json";
        log.info("Resource file exists: {}", resourceLoader.getResource(resourcePath).exists());

        try (InputStream inputStream = resourceLoader.getResource(resourcePath).getInputStream()) {
            if (inputStream == null) {
                log.error("StructureDefinition file not found at: {}", resourcePath);
                throw new IOException("StructureDefinition file not found: " + resourcePath);
            }
            StructureDefinition structureDefinition = (StructureDefinition) fhirContext.newJsonParser().parseResource(inputStream);
            support.addStructureDefinition(structureDefinition);
            log.info("Loaded StructureDefinition: mi-practitioner-persistencia");
        } catch (IOException e) {
            log.error("Failed to load StructureDefinition: {}", e.getMessage());
            throw e;
        }
        return support;
    }

    /**
     * Configura el validador FHIR con soporte para perfiles y terminologías.
     *
     * @param fhirContext Contexto FHIR utilizado en la validación.
     * @param prePopulatedValidationSupport Soporte de validación predefinido con perfiles personalizados.
     * @return Instancia de {@link FhirValidator} configurada.
     */
    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext, PrePopulatedValidationSupport prePopulatedValidationSupport) {
        ValidationSupportChain validationSupportChain = new ValidationSupportChain(
                prePopulatedValidationSupport,
                new DefaultProfileValidationSupport(fhirContext),
                new InMemoryTerminologyServerValidationSupport(fhirContext),
                new CommonCodeSystemsTerminologyService(fhirContext)
        );

        FhirValidator validator = fhirContext.newValidator();
        validator.registerValidatorModule(new org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator(validationSupportChain));
        return validator;
    }
}