package com.myobservation.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import com.myobservation.fhir.service.StructureDefinitionLoader;
import com.myobservation.fhir.service.StructureDefinitionLoaderServiceImpl;
import jakarta.annotation.PostConstruct;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Esta clase configura los componentes de FHIR en la aplicación usando la librería
 * HAPI FHIR que permite la validación y manipulación de recursos FHIR
 */

@Configuration
public class FHIRConfig {

    private static final Logger logger = LoggerFactory.getLogger(StructureDefinitionLoaderServiceImpl.class);


    /**
     * Configura cadena de soporte para validación de recursos FHIR
     * Utiliza PrePopulatedValidationSupport para precargar validaciones específicas
     * @return
     */
    @Bean
    public ValidationSupportChain validationSupportChain() {
        return new ValidationSupportChain();
    }

    /**
     * Define el contexto FHIR R4 que es la versión utilizada.
     * @return Contexto
     */
    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    /**
     * Crea una instancia de FhirValidator para validar estructuras de datos FHIR
     * @param fhirContext
     * @return
     */
    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext) {
        return fhirContext.newValidator();
    }

}
