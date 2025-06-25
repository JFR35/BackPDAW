package com.myobservation.fhirbridge.config;



import ca.uhn.fhir.parser.IParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ca.uhn.fhir.context.FhirContext;

/**
 * Configuración de parsers FHIR para conversión entre formatos
 */
@Configuration
public class FHIRParserConfig {

    /**
     * Crea un parser JSON para FHIR
     * @param fhirContext Contexto FHIR
     * @return Parser JSON para FHIR
     */
    @Bean
    public IParser fhirJsonParser(FhirContext fhirContext) {
        return fhirContext.newJsonParser().setPrettyPrint(true);
    }

    /**
     * Crea un parser XML para FHIR
     * @param fhirContext Contexto FHIR
     * @return Parser XML para FHIR
     */
    @Bean
    public IParser fhirXmlParser(FhirContext fhirContext) {
        return fhirContext.newXmlParser().setPrettyPrint(true);
    }
}