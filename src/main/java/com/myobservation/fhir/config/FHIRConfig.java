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
    public FhirContext fhirContext() {
        FhirContext fhirContext = FhirContext.forR4();

        ValidationSupportChain validationSupportChain = new ValidationSupportChain();
        validationSupportChain.addValidationSupport(new PrePopulatedValidationSupport(fhirContext));

        fhirContext.setValidationSupport(validationSupportChain);

        return fhirContext;
    }

    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext) {
        return fhirContext.newValidator();
    }
}
