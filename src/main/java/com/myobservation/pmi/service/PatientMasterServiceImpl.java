package com.myobservation.pmi.service;
/*
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.myobservation.ehrbridge.service.EhrBaseService;
import com.myobservation.pmi.mapper.PatientMapper;
import com.myobservation.pmi.model.dto.PMIRequest;
import com.myobservation.pmi.model.dto.PMIResponse;
import com.myobservation.pmi.model.entity.PatientMasterIndex;
import com.myobservation.fhirbridge.service.FHIRBaseService;
import com.myobservation.pmi.repository.PatientMasterRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PatientMasterServiceImpl implements PatientMasterService {
    private final PatientMasterRepository patientMasterRepository;
    private final PatientMapper patientMapper;
    private final FHIRBaseService fhirBaseService;
    private final EhrBaseService ehrbaseService;

    public PatientMasterServiceImpl(PatientMasterRepository patientMasterRepository, PatientMapper patientMapper, FHIRBaseService fhirBaseService, EhrBaseService ehrbaseService) {
        this.patientMasterRepository = patientMasterRepository;
        this.patientMapper = patientMapper;
        this.fhirBaseService = fhirBaseService;
        this.ehrbaseService = ehrbaseService;
    }


    @Override
    public List<PMIResponse> getAllPatients() {
        return null;
    }

    @Override
    public Optional<PMIResponse> getPatientById(Long patientId) {
        return Optional.empty();
    }

    @Override
    public PMIResponse createPatient(PMIRequest patientRequest) throws PatientMapperAlreadyExists {
        if (patientMasterRepository.findByInternalId(patientRequest.getInternalId()).isPresent()) {
            throw new PatientMapperAlreadyExists("Paciente con internalId '" + patientRequest.getInternalId() + "' ya existe");
        }
        if (patientMasterRepository.findByNationalId(patientRequest.getNationalId()).isPresent()) {
            throw new PatientMapperAlreadyExists("Paciente con nationalId '" + patientRequest.getNationalId() + "' ya existe");
        }

        // Crear Patient en Aidbox
        String fhirId = createFhirPatient(patientRequest);

        // Crear EHR en EHRbase
        String ehrId = createEhr();

        // Mapear DTO a entidad
        PatientMasterIndex entity = patientMapper.toEntity(patientRequest);
        entity.setFhirId(fhirId);
        entity.setEhrId(UUID.fromString(ehrId));

        // Guardar en BD
        PatientMasterIndex saved = patientMasterRepository.save(entity);
        return patientMapper.toResponse(saved);
    }

    @Override
    public Optional<PMIResponse> updatePatient(Long patientId, PMIRequest patientRequest) {
        return Optional.empty();
    }

    @Override
    public boolean deletePatient(Long patientId) {
        return false;
    }

    private String createFhirPatient(PMIRequest patientRequest) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Añadir autenticación según Aidbox

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode patientJson = mapper.createObjectNode();
            patientJson.put("resourceType", "Patient");
            ArrayNode nameArray = mapper.createArrayNode();
            ObjectNode name = mapper.createObjectNode();
            name.put("use", "official");
            name.putArray("given").add(patientRequest.getFirstName());
            name.put("family", patientRequest.getLastName());
            nameArray.add(name);
            patientJson.set("name", nameArray);
            patientJson.put("gender", "unknown"); // Ajustar según necesidad
            patientJson.put("birthDate", "1985-06-20"); // Ejemplo, ajustar según datos

            HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(patientJson), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    fhirBaseUrl + "/Patient",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            JsonNode responseJson = mapper.readTree(response.getBody());
            return responseJson.path("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear Patient en Aidbox: " + e.getMessage());
        }
    }

    private String createEhr() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Añadir autenticación según EHRbase

            HttpEntity<String> request = new HttpEntity<>("{}", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    ehrbaseBaseUrl + "/ehr",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(response.getBody());
            return responseJson.path("ehr_id").path("value").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear EHR en EHRbase: " + e.getMessage());
        }
    }
}

 */