package com.myobservation.ehrbridge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myobservation.ehrbridge.pojos.BloodPressureComposition;
import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.ehrbridge.pojos.definition.*;
import com.myobservation.empi.model.dto.BloodPressureMeasurementDto;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Hay que refactorizar por completo esta clase
 */
@Service
public class EhrBaseService {

    private static final Logger logger = LoggerFactory.getLogger(EhrBaseService.class);
    private static final String TEMPLATE_ID = "blood_pressure";
    private final RestTemplate restTemplate;
    private final OpenEhrClient openEhrClient;
    private final TemplateProvider templateProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Aunque las variables están definidas en Config las repito para mayor seguridad
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

    /**
     * Consulta AQL para obtener el historial de mediciones de presión sanguínea.
     * @param ehrId El ID del EHR del paciente.
     * @return Lista de BloodPressureMeasurementDto con las mediciones.
     */
    public List<BloodPressureMeasurementDto> queryBloodPressureHistory(String ehrId) {
        // Validar ehrId para prevenir inyecciones AQL
        if (ehrId == null || !isValidUUID(ehrId)) {
            throw new IllegalArgumentException("Invalid ehrId: " + ehrId);
        }

        // Construir la consulta AQL
        String aqlQuery = "SELECT " +
                "c/context/start_time as measurement_time, " +
                "bp/data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/magnitude as systolic_magnitude, " +
                "bp/data[at0001]/events[at0006]/data[at0003]/items[at0004]/value/units as systolic_unit, " +
                "bp/data[at0001]/events[at0006]/data[at0003]/items[at0005]/value/magnitude as diastolic_magnitude, " +
                "bp/data[at0001]/events[at0006]/data[at0003]/items[at0005]/value/units as diastolic_unit, " +
                "bp/protocol[at0011]/items[at0014]/value/value as location " +
                "FROM EHR e " +
                "CONTAINS COMPOSITION c " +
                "CONTAINS OBSERVATION bp[openEHR-EHR-OBSERVATION.blood_pressure.v2] " +
                "WHERE e/ehr_id/value = '" + ehrId + "' " +
                "ORDER BY c/context/start_time DESC";

        // Preparar la solicitud
        HttpHeaders headers = createAuthenticatedHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, String> requestBody = new LinkedHashMap<>();
        requestBody.put("q", aqlQuery);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        String queryUrl = ehrBaseUrl + "/rest/openehr/v1/query/aql";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(queryUrl, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<List<Object>> rows = (List<List<Object>>) response.getBody().get("rows");
                List<BloodPressureMeasurementDto> measurements = new ArrayList<>();

                if (rows != null) {
                    for (List<Object> row : rows) {
                        // Mapear los resultados de la consulta AQL al DTO
                        LinkedHashMap<String, String> dateTimeMap = (LinkedHashMap<String, String>) row.get(0);
                        OffsetDateTime measurementDate = OffsetDateTime.parse(dateTimeMap.get("value"));

                        measurements.add(new BloodPressureMeasurementDto(
                                measurementDate,
                                (Double) row.get(1), // systolic_magnitude
                                (String) row.get(2), // systolic_unit
                                (Double) row.get(3), // diastolic_magnitude
                                (String) row.get(4), // diastolic_unit
                                (String) row.get(5), // location
                                null // measuredBy se establecerá más adelante en PatientService
                        ));
                    }
                }
                logger.info("Successfully retrieved {} blood pressure measurements for ehrId: {}", measurements.size(), ehrId);
                return measurements;
            } else {
                logger.error("Error querying EHRBase for ehrId {}: Status {}", ehrId, response.getStatusCode());
                throw new RuntimeException("Error querying EHRBase: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to query blood pressure history for ehrId {}: {}", ehrId, e.getMessage());
            throw new RuntimeException("Failed to query EHRBase: " + e.getMessage(), e);
        }
    }

    /**
     * Valida si una cadena es un UUID válido.
     * @param uuid La cadena a validar.
     * @return true si es un UUID válido, false en caso contrario.
     */
    private boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /*
    public void registerTemplate() {
        try {
            File templateFile = new File(templatePath + "/presion_sanguinea.opt");
            if (!templateFile.exists()) {
                throw new RuntimeException("Template file not found: " + templateFile.getAbsolutePath());
            }
            openEhrClient.templateEndpoint().createTemplate(templateFile);
            logger.info("Template 'presion_sanguinea' registered successfully.");
        } catch (Exception e) {
            logger.error("Failed to register template", e);
            throw new RuntimeException("Failed to register template", e);
        }
    }

     */

    // Listar las plantillas que ya han sido registrados
    public void registerTemplate() {
        try {
            File templateFile = new File(templatePath + "/presion_sanguinea.opt");
            if (!templateFile.exists()) {
                logger.warn("Template file 'presion_sanguinea.opt' not found: {}. The application will use the 'blood_pressure' template already registered in EHRBase.", templateFile.getAbsolutePath());
            } else {
                logger.info("Template file 'presion_sanguinea.opt' found, but 'blood_pressure' is being used from EHRBase.");
            }
        } catch (Exception e) {
            logger.error("Failed to verify template file", e);
            throw new RuntimeException("Failed to verify template file", e);
        }
    }

    // Verifica la conexión establecida con la API de EHRbase
    public boolean verifyConnection() {
        try {
            openEhrClient.ehrEndpoint().getEhrStatus(UUID.randomUUID());
            return true;
        } catch (Exception e) {
            logger.error("Failed to connect to EHRbase or authentication issue: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Crear una nueva composición
     * @param requestDTO Recibe el requestDTO con la estructura de datos
     * @param ehrId Recibe el ehrId creado para identificar la composición
     * @return
     */
    public String createBloodPressureComposition(BloodPressureRequestDTO requestDTO, String ehrId) {
        try {
            // Busca el template necesacion para crear la composición
            Optional<?> template = templateProvider.find(TEMPLATE_ID);
            if (template.isEmpty()) {
                throw new RuntimeException("Template 'presion_sanguinea' is not registered in EHRbase.");
            }
            // Genera el UUID del EHR, creando uno nuevo en caso de no proporcionarse
            UUID ehrUUID = ehrId != null ? UUID.fromString(ehrId) : createPatientEhr(requestDTO.getPatientId());
            // Crea la composición con los datos proporcionados
            BloodPressureComposition composition = createBPComposition(requestDTO, ehrUUID);
            // Guarda la composición y retorna su ID
            return saveComposition(composition, ehrUUID);
        } catch (Exception e) {
            logger.error("Failed to create blood pressure composition for ehrId {}: {}", ehrId, e.getMessage(), e);
            throw new RuntimeException("Failed to create blood pressure composition", e);
        }
    }

    // Metodo para crear un nuevo registro
    public UUID createPatientEhr(String patientId) {
        try {
            // Obtiene el endpoint de EHRbase y crea un nuevo EHR
            EhrEndpoint ehrEndpoint = openEhrClient.ehrEndpoint();
            UUID ehrId = ehrEndpoint.createEhr();
            logger.info("Created EHR for patient: {} with ID: {}", patientId, ehrId);
            return ehrId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create EHR for patient: " + patientId, e);
        }
    }

    // Método privado para construir la composición de presion arterial
    private BloodPressureComposition createBPComposition(BloodPressureRequestDTO requestDTO, UUID ehrId) {
        // Inicializa la composición
        BloodPressureComposition composition = new BloodPressureComposition();
        // Configura idioma, y territorio
        composition.setLanguage(Language.EN);
        composition.setTerritory(Territory.ES);
        composition.setCategoryDefiningCode(Category.EVENT);
        composition.setSettingDefiningCode(Setting.OTHER_CARE);
        // Asigna la fecha y hora de la medición
        composition.setStartTimeValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));
        // Establece el practitioner autor de la composión
        PartyIdentified composer = new PartyIdentified(); // El sujeto de la medición es el propio paciente
        composer.setName(requestDTO.getComposerName());
        composition.setComposer(composer);

        BloodPressureObservation bpObservation = new BloodPressureObservation();
        bpObservation.setSubject(new PartySelf());
        bpObservation.setOriginValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));
        bpObservation.setLanguage(Language.EN);
        // Determina la ubicación física donde se produce la medición
        BloodPressureLocationOfMeasurementDvCodedText locationOfMeasurement = new BloodPressureLocationOfMeasurementDvCodedText();
        locationOfMeasurement.setLocationOfMeasurementDefiningCode(LocationOfMeasurementDefiningCode.RIGHT_ARM);
        bpObservation.setLocationOfMeasurement(locationOfMeasurement);
        // Método utilizado
        bpObservation.setMethodDefiningCode(MethodDefiningCode.AUSCULTATION);
        // Crea un nuevo evento de la medición
        BloodPressureAnyEventPointEvent event = new BloodPressureAnyEventPointEvent();
        event.setSystolicMagnitude(requestDTO.getSystolic());
        event.setSystolicUnits("mm[Hg]");
        event.setDiastolicMagnitude(requestDTO.getDiastolic());
        event.setDiastolicUnits("mm[Hg]");

        event.setTimeValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));

        if (requestDTO.getMeanArterialPressure() != null) {
            // Añadir metododos para calcular la presión arterial media
            // event.setMeanArterialPressureMagnitude(requestDTO.getMeanArterialPressure());
            // event.setMeanArterialPressureUnits("mm[Hg]");
        }
        // Asigna el evento de medición a la observación de presión arterial
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
        // headers.setBasicAuth("username", "password") se envían en el header de la request;
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

            HttpHeaders headers = createAuthenticatedHeaders();
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
            logger.error("Error retrieving composition to ehrId {}: {}", ehrId, e.getMessage());
            return Collections.emptyList();
        }
    }

}