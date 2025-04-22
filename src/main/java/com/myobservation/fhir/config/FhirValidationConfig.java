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

@Configuration
public class FhirValidationConfig {

    private static final Logger log = LoggerFactory.getLogger(FhirValidationConfig.class);

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public PrePopulatedValidationSupport prePopulatedValidationSupport(FhirContext fhirContext, ResourceLoader resourceLoader) throws IOException {
        PrePopulatedValidationSupport support = new PrePopulatedValidationSupport(fhirContext);
        //String resourcePath = "classpath:/fhir-profiles/StructureDefinition-mi-paciente-persistencia.json";
        String resourcePath = "classpath:/fhir-profiles/StructureDefinition-mi-practitioner-persistencia.json";
        log.info("Resource file exists: {}", resourceLoader.getResource(resourcePath).exists());

        try (InputStream inputStream = resourceLoader.getResource(resourcePath).getInputStream()) {
            if (inputStream == null) {
                log.error("StructureDefinition file not found at: {}", resourcePath);
                throw new IOException("StructureDefinition file not found: " + resourcePath);
            }
            StructureDefinition structureDefinition = (StructureDefinition) fhirContext.newJsonParser().parseResource(inputStream);
            support.addStructureDefinition(structureDefinition);
            log.info("Loaded StructureDefinition: mi-paciente-persistencia");
        } catch (IOException e) {
            log.error("Failed to load StructureDefinition: {}", e.getMessage());
            throw e;
        }
        return support;
    }

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