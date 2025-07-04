package com.myobservation.fhirbridge.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import jakarta.annotation.PostConstruct;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Clase para cargar el structureDefinition y validar con HAPI FHIR
 */
@Service
public class StructureDefinitionLoaderServiceImpl implements StructureDefinitionLoader {
    private static final Logger logger = LoggerFactory.getLogger(StructureDefinitionLoaderServiceImpl.class);
    private final FhirContext fhirContext;
    private final IParser jsonParser;
    private final ValidationSupportChain validationSupportChain;


    @Autowired
    public StructureDefinitionLoaderServiceImpl(FhirContext fhirContext, ValidationSupportChain validationSupportChain) {
        this.fhirContext = fhirContext;
        this.jsonParser = fhirContext.newJsonParser();
        this.validationSupportChain = validationSupportChain;
    }

    @Override
    public void loadStructureDefinition(String jsonProfile) {
        try {
            StructureDefinition structureDefinition = jsonParser.parseResource(StructureDefinition.class, jsonProfile);
            PrePopulatedValidationSupport prePopulatedValidationSupport = new PrePopulatedValidationSupport(fhirContext);
            prePopulatedValidationSupport.addStructureDefinition(structureDefinition);
            validationSupportChain.addValidationSupport(prePopulatedValidationSupport);
            logger.info("StructureDefinition '{}' precargada correctamente en HAPI FHIR.", structureDefinition.getUrl());
        } catch (Exception e) {
            logger.error("Error al cargar el StructureDefinition en HAPI FHIR", e);
            throw new RuntimeException("Error al cargar el StructureDefinition", e);
        }
    }

    @Override
    public String loadJsonProfileFromClasspath(String resourcePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("No se encontró el archivo '" + resourcePath + "' en el classpath.");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @PostConstruct
    public void init() {
        logger.info("INIT StructureDefinitionLoaderServiceImpl...");
        List<String> profiles = List.of(
                // ArrayList dinámico
                "fhir-profiles/mi-paciente-persistencia.json",
                "fhir-profiles/mi-practitioner-persistencia.json"
        );
        PrePopulatedValidationSupport prePopulatedSupport = new PrePopulatedValidationSupport(fhirContext);
        for (String resourcePath : profiles) {
            try {
                String jsonProfile = loadJsonProfileFromClasspath(resourcePath);
                StructureDefinition structureDefinition = jsonParser.parseResource(StructureDefinition.class, jsonProfile);
                prePopulatedSupport.addStructureDefinition(structureDefinition);
                logger.info("El recurso '{}' se cargó exitosamente.", resourcePath);
            } catch (Exception e) {
                logger.error("Error al cargar el perfil FHIR desde el classpath: {}", resourcePath, e);
                throw new RuntimeException("Error al cargar el StructureDefinition: " + resourcePath, e);
            }
        }
        validationSupportChain.addValidationSupport(prePopulatedSupport);
        fhirContext.setValidationSupport(validationSupportChain);
        logger.info("Carga completa de StructureDefinitions.");
    }
}