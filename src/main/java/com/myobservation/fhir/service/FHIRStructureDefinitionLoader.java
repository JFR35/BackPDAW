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
    public FHIRStructureDefinitionLoader(FhirContext fhirContext) {
        this.fhirContext = fhirContext;
        this.jsonParser = fhirContext.newJsonParser();
        this.validationSupportChain = (ValidationSupportChain) fhirContext.getValidationSupport();
    }

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

            logger.info("✅ StructureDefinition '{}' precargado correctamente en HAPI FHIR.",
                    structureDefinition.getUrl());
        } catch (Exception e) {
            logger.error("❌ Error al cargar el StructureDefinition en HAPI FHIR", e);
        }
    }
}
