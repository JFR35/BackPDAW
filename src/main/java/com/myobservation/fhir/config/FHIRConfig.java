package com.myobservation.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FHIRConfig {

    @Bean
    public ValidationSupportChain validationSupportChain(FhirContext fhirContext) {
        ValidationSupportChain chain = new ValidationSupportChain();
        chain.addValidationSupport(new PrePopulatedValidationSupport(fhirContext));
        fhirContext.setValidationSupport(chain);
        return chain;
    }

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext) {
        return fhirContext.newValidator();
    }
}
