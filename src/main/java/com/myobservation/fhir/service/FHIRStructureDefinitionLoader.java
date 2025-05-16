package com.myobservation.fhir.service;

import ca.uhn.fhir.context.FhirContext;
import jakarta.annotation.PostConstruct;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.parser.IParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FHIRStructureDefinitionLoader {

    private static final Logger logger = LoggerFactory.getLogger(FHIRStructureDefinitionLoader.class);

    private final FhirContext fhirContext;
    private final IParser jsonParser;
    private final ValidationSupportChain validationSupportChain;

    @Autowired
    public FHIRStructureDefinitionLoader(FhirContext fhirContext, ValidationSupportChain validationSupportChain) {
        this.fhirContext = fhirContext;
        this.jsonParser = fhirContext.newJsonParser();
        this.validationSupportChain = validationSupportChain;
    }

    @PostConstruct
    public void init() {
        String resourcePath = "fhir-profiles/mi-paciente-persistencia.json";
        logger.info("Intentando cargar recurso desde: {}", resourcePath);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.error("No se encontró el archivo '{}' en el classpath.", resourcePath);
                return;
            }

            String jsonProfile = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            logger.info("✅ Recurso cargado exitosamente: {}", resourcePath);
            loadStructureDefinition(jsonProfile);

        } catch (Exception e) {
            logger.error("Error al cargar el perfil FHIR desde el classpath: {}", resourcePath, e);
            throw new RuntimeException("Error al cargar el StructureDefinition", e);
        }
    }

    public void loadStructureDefinition(String jsonProfile) {
        try {
            StructureDefinition structureDefinition = jsonParser.parseResource(StructureDefinition.class, jsonProfile);

            PrePopulatedValidationSupport prePopulatedValidationSupport = new PrePopulatedValidationSupport(fhirContext);
            prePopulatedValidationSupport.addStructureDefinition(structureDefinition);

            validationSupportChain.addValidationSupport(prePopulatedValidationSupport);

            logger.info("✅ StructureDefinition '{}' precargado correctamente en HAPI FHIR.",
                    structureDefinition.getUrl());
        } catch (Exception e) {
            logger.error("Error al cargar el StructureDefinition en HAPI FHIR", e);
        }
    }
    /* Ruta para cargar localmente el structureDefinition
    @PostConstruct
    public void init() {
        try {
            String path = "src/main/resources/fhir-profiles/mi-paciente-persistencia.json";
            String jsonProfile = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            loadStructureDefinition(jsonProfile);
        } catch (IOException e) {
            logger.error("Error al cargar el perfil FHIR", e);
        }
    }

    public void loadStructureDefinition(String jsonProfile) {
        try {
            StructureDefinition structureDefinition = jsonParser.parseResource(StructureDefinition.class, jsonProfile);

            PrePopulatedValidationSupport prePopulatedValidationSupport = new PrePopulatedValidationSupport(fhirContext);
            prePopulatedValidationSupport.addStructureDefinition(structureDefinition);

            validationSupportChain.addValidationSupport(prePopulatedValidationSupport);

            logger.info("StructureDefinition '{}' precargado correctamente en HAPI FHIR.",
                    structureDefinition.getUrl());
        } catch (Exception e) {
            logger.error("Error al cargar el StructureDefinition en HAPI FHIR", e);
        }
    }

     */
}
