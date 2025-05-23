package com.myobservation.ehrbridge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myobservation.ehrbridge.pojos.BloodPressureComposition;
import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.ehrbridge.pojos.definition.*;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.rm.generic.PartySelf;
import jakarta.annotation.PostConstruct;
import javassist.NotFoundException;
import org.ehrbase.openehr.sdk.client.openehrclient.EhrEndpoint;
import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClient;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Category;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Language;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Setting;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Territory;
import org.ehrbase.openehr.sdk.webtemplate.templateprovider.TemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class EhrBaseService {

    private static final Logger logger = LoggerFactory.getLogger(EhrBaseService.class);
    private static final String TEMPLATE_ID = "blood_pressure";
    private final RestTemplate restTemplate;
    private final OpenEhrClient openEhrClient;
    private final TemplateProvider templateProvider;

    @Value("${ehrbase.url}")
    private String ehrBaseUrl;

    @Value("${ehrbase.username}")
    private String ehrBaseUsername;

    @Value("${ehrbase.password}")
    private String ehrBasePassword;

    @Value("${ehrbase.templates.path}")
    private String templatePath;


    public EhrBaseService(RestTemplate restTemplate, OpenEhrClient openEhrClient, TemplateProvider templateProvider) {
        this.restTemplate = restTemplate;
        this.openEhrClient = openEhrClient;
        this.templateProvider = templateProvider;
        logger.info("EHR client and TemplateProvider injected successfully into EhrBaseService.");
    }

    @PostConstruct
    public void init() {
        registerTemplate();
    }

    public void registerTemplate() {
        try {
            File templateFile = new File(templatePath + "/presion_sanguinea.opt");
            if (!templateFile.exists()) {
                throw new RuntimeException("Template file not found: " + templateFile.getAbsolutePath());
            }
            //openEhrClient.templateEndpoint().createTemplate(templateFile);
            logger.info("Template 'presion_sanguinea' registered successfully.");
        } catch (Exception e) {
            logger.error("Failed to register template", e);
            throw new RuntimeException("Failed to register template", e);
        }
    }


    public boolean verifyConnection() {
        try {
            openEhrClient.ehrEndpoint().getEhrStatus(UUID.randomUUID());
            return true;
        } catch (Exception e) {
            logger.error("Failed to connect to EHRbase or authentication issue: {}", e.getMessage());
            return false;
        }
    }

    public String createBloodPressureComposition(BloodPressureRequestDTO requestDTO, String ehrId) {
        try {
            Optional<?> template = templateProvider.find(TEMPLATE_ID);
            if (template.isEmpty()) {
                throw new RuntimeException("La plantilla 'presion_sanguinea' no está registrada en EHRbase.");
            }
            UUID ehrUUID = ehrId != null ? UUID.fromString(ehrId) : createPatientEhr(requestDTO.getPatientId());
            BloodPressureComposition composition = createBPComposition(requestDTO, ehrUUID);
            return saveComposition(composition, ehrUUID);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create blood pressure composition", e);
        }
    }

    private UUID createPatientEhr(String patientId) {
        try {
            EhrEndpoint ehrEndpoint = openEhrClient.ehrEndpoint();
            UUID ehrId = ehrEndpoint.createEhr();
            logger.info("Created EHR for patient: {} with ID: {}", patientId, ehrId);
            return ehrId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create EHR for patient: " + patientId, e);
        }
    }

    private BloodPressureComposition createBPComposition(BloodPressureRequestDTO requestDTO, UUID ehrId) {
        BloodPressureComposition composition = new BloodPressureComposition();

        composition.setLanguage(Language.EN);
        composition.setTerritory(Territory.ES);
        composition.setCategoryDefiningCode(Category.EVENT);
        composition.setSettingDefiningCode(Setting.OTHER_CARE);

        composition.setStartTimeValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));

        PartyIdentified composer = new PartyIdentified();
        composer.setName(requestDTO.getComposerName());
        composition.setComposer(composer);

        BloodPressureObservation bpObservation = new BloodPressureObservation();
        bpObservation.setSubject(new PartySelf());
        bpObservation.setOriginValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));
        bpObservation.setLanguage(Language.EN);

        BloodPressureLocationOfMeasurementDvCodedText locationOfMeasurement = new BloodPressureLocationOfMeasurementDvCodedText();
        locationOfMeasurement.setLocationOfMeasurementDefiningCode(LocationOfMeasurementDefiningCode.RIGHT_ARM);
        bpObservation.setLocationOfMeasurement(locationOfMeasurement);

        bpObservation.setMethodDefiningCode(MethodDefiningCode.AUSCULTATION);

        BloodPressureAnyEventPointEvent event = new BloodPressureAnyEventPointEvent();
        event.setSystolicMagnitude(requestDTO.getSystolic());
        event.setSystolicUnits("mm[Hg]");
        event.setDiastolicMagnitude(requestDTO.getDiastolic());
        event.setDiastolicUnits("mm[Hg]");

        event.setTimeValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));

        if (requestDTO.getMeanArterialPressure() != null) {
            // Descomentar si los métodos existen
            // event.setMeanArterialPressureMagnitude(requestDTO.getMeanArterialPressure());
            // event.setMeanArterialPressureUnits("mm[Hg]");
        }
        bpObservation.setAnyEvent(Collections.singletonList(event));
        composition.setBloodPressure(bpObservation);

        return composition;
    }

    private String saveComposition(BloodPressureComposition composition, UUID ehrId) {
        try {
            BloodPressureComposition savedComposition = openEhrClient.compositionEndpoint(ehrId)
                    .mergeCompositionEntity(composition);
            String compositionId = savedComposition.getVersionUid().getValue();
            logger.info("Composition saved with ID: {}", compositionId);
            return compositionId;
        } catch (Exception e) {
            logger.error("Failed to save composition to EHRBase", e);
            throw new RuntimeException("Failed to save composition to EHRBase", e);
        }
    }

    private UUID extractCompositionUuid(String compositionId) {
        // El compositionId puede venir en formato "UUID::domain::version"
        // Extraemos solo la parte UUID
        String[] parts = compositionId.split("::");
        return UUID.fromString(parts[0]);
    }

    public String getComposition(String ehrId, String compositionId, String format) throws NotFoundException {
        try {
            UUID ehrUUID = validateAndParseUUID(ehrId, "EHR ID");
            UUID compositionUUID = extractCompositionUuid(compositionId);

            // Opciones para diferentes formatos
            if ("RAW".equalsIgnoreCase(format)) {
                Composition rawComposition = openEhrClient.compositionEndpoint(ehrUUID)
                        .findRaw(compositionUUID)
                        .orElseThrow(() -> new NotFoundException("Composition not found"));
                return new ObjectMapper().writeValueAsString(rawComposition);
            } else if ("FLAT".equalsIgnoreCase(format)) {
                // Alternativa para obtener en formato FLAT
                return getCompositionAsFlatJson(ehrUUID, compositionUUID);
            } else {
                // Por defecto devolvemos la composición en formato estructurado
                BloodPressureComposition composition = openEhrClient.compositionEndpoint(ehrUUID)
                        .find(compositionUUID, BloodPressureComposition.class)
                        .orElseThrow(() -> new NotFoundException("Composition not found"));
                return new ObjectMapper().writeValueAsString(composition);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing composition JSON", e);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to retrieve composition", e);
            throw new RuntimeException("Failed to retrieve composition", e);
        }
    }

    private UUID validateAndParseUUID(String uuid, String fieldName) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + " format");
        }
    }

    private void validateCompositionId(String compositionId) {
        if (compositionId == null || compositionId.isEmpty()) {
            throw new IllegalArgumentException("Composition ID cannot be empty");
        }
        // Aquí podrías añadir más validaciones específicas para el formato de compositionId
    }

    public String getComposition(String ehrId, String compositionId) {
        try {
            // Validar parámetros
            if (ehrId == null || compositionId == null) {
                throw new IllegalArgumentException("ehrId and compositionId are required");
            }

            // Configurar la llamada a la API REST de EHRbase
            RestTemplate restTemplate = new RestTemplate();
            String ehrbaseUrl = String.format("%s/rest/openehr/v1/ehr/%s/composition/%s?format=FLAT",
                    this.ehrBaseUrl, ehrId, compositionId);

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(ehrBaseUsername, ehrBasePassword);
            headers.set("Accept", "application/openehr.wt.flat.schema+json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Hacer la solicitud GET
            String flatComposition = restTemplate.exchange(ehrbaseUrl, HttpMethod.GET, entity, String.class).getBody();

            logger.info("Retrieved composition with ID: {} for EHR: {}", compositionId, ehrId);
            return flatComposition;

        } catch (IllegalArgumentException e) {
            logger.error("Invalid input parameters: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to retrieve composition with ID: {} for EHR: {}", compositionId, ehrId, e);
            throw new RuntimeException("Failed to retrieve composition from EHRbase", e);
        }
    }

    private String convertCompositionToFormat(Composition composition, String format) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        if ("RAW".equalsIgnoreCase(format)) {
            return objectMapper.writeValueAsString(composition);
        } else if ("STRUCTURED".equalsIgnoreCase(format)) {
            // Implementar conversión a formato estructurado si es necesario
            return objectMapper.writeValueAsString(composition);
        } else {
            // Por defecto FLAT
            return objectMapper.writeValueAsString(composition);
        }
    }

    private String getCompositionAsFlatJson(UUID ehrUUID, UUID compositionUUID) {
        try {
            // Configurar la llamada a la API REST de EHRbase
            RestTemplate restTemplate = new RestTemplate();
            String ehrbaseUrl = String.format("%s/rest/openehr/v1/ehr/%s/composition/%s?format=FLAT",
                    this.ehrBaseUrl, ehrUUID.toString(), compositionUUID.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(ehrBaseUsername, ehrBasePassword);
            headers.set("Accept", "application/openehr.wt.flat.schema+json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Hacer la solicitud GET
            String flatComposition = restTemplate.exchange(ehrbaseUrl, HttpMethod.GET, entity, String.class).getBody();

            logger.info("Retrieved FLAT composition for EHR: {} with composition ID: {}", ehrUUID, compositionUUID);
            return flatComposition;

        } catch (Exception e) {
            logger.error("Failed to retrieve FLAT composition for EHR: {} with composition ID: {}", ehrUUID, compositionUUID, e);
            throw new RuntimeException("Failed to retrieve FLAT composition from EHRbase", e);
        }
    }

    /**
     * Implementacion para Get Ehr_ID
     * @return
     */
    private HttpHeaders createAuthenticatedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Añadir autenticación según tu configuración de EHRbase
        // headers.setBasicAuth("username", "password");
        return headers;
    }

    /**
     * Método para obtener el ehr_id con consulta AQL
     * @param ehrId
     * @return
     */
    public List<String> getCompositionsByEhrId(String ehrId) {
        try {
            String aqlQuery = "SELECT c FROM EHR e CONTAINS COMPOSITION c WHERE e/ehr_id/value = '" + ehrId + "'";

            HttpHeaders headers = createAuthenticatedHeaders(); // <-- Aquí corriges el error
            HttpEntity<String> request = new HttpEntity<>(aqlQuery, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    ehrBaseUrl + "/query/aql",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode result = mapper.readTree(response.getBody());
            List<String> compositions = new ArrayList<>();
            JsonNode rows = result.path("rows");
            for (JsonNode row : rows) {
                compositions.add(row.get(0).toString());
            }
            return compositions;
        } catch (Exception e) {
            logger.error("Error al obtener composiciones para ehrId {}: {}", ehrId, e.getMessage());
            return Collections.emptyList();
        }
    }


}