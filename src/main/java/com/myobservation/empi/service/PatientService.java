// src/main/java/com/myobservation/empi/service/PatientService.java
package com.myobservation.empi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.ehrbridge.service.EhrBaseService;
import com.myobservation.fhirbridge.service.FHIRBaseService;
import com.myobservation.empi.model.entity.PatientMasterIndex;
import com.myobservation.empi.model.entity.PractitionerMasterIndex;
import com.myobservation.empi.model.entity.Visit;
import com.myobservation.empi.model.dto.BloodPressureMeasurementDto;
import com.myobservation.empi.model.dto.PatientResponseDTO; // <--- Importa el DTO
import com.myobservation.empi.repository.PatientMasterRepository;
import com.myobservation.empi.repository.PractitionerRepository;
import com.myobservation.empi.repository.VisitMasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Usar transational ya que hay operaciones en varias tablas y de distintos proveedores
 */
@Service
public class PatientService {

    private final PatientMasterRepository pmiRepository;
    private final PractitionerRepository practitionerRepository;
    private final VisitMasterRepository visitMasterRepository;
    private final FHIRBaseService fhirBaseService;
    private final EhrBaseService ehrBaseService;
    private final ObjectMapper objectMapper;

    public PatientService(PatientMasterRepository pmiRepository,
                          PractitionerRepository practitionerRepository,
                          VisitMasterRepository visitMasterRepository,
                          FHIRBaseService fhirBaseService,
                          EhrBaseService ehrBaseService,
                          ObjectMapper objectMapper) {
        this.pmiRepository = pmiRepository;
        this.practitionerRepository = practitionerRepository;
        this.visitMasterRepository = visitMasterRepository;
        this.fhirBaseService = fhirBaseService;
        this.ehrBaseService = ehrBaseService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PatientResponseDTO registerNewPatient(String patientJson, String nationalId) {
        Optional<PatientMasterIndex> existingPmi = pmiRepository.findByNationalId(nationalId);
        if (existingPmi.isPresent()) {
            throw new RuntimeException("Patient with DNI " + nationalId + " already exists.");
        }

        String fhirResponseJson = fhirBaseService.storePatient(patientJson);
        String fhirId;
        try {
            JsonNode rootNode = objectMapper.readTree(fhirResponseJson);
            fhirId = rootNode.path("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("Parsing response error from Aidbox to obtain FHIR ID", e);
        }

        UUID ehrId = ehrBaseService.createPatientEhr(nationalId);

        PatientMasterIndex pmiEntry = new PatientMasterIndex();
        pmiEntry.setNationalId(nationalId);
        pmiEntry.setFhirId(fhirId);
        pmiEntry.setEhrId(String.valueOf(ehrId));

        PatientMasterIndex savedPmi = pmiRepository.save(pmiEntry);
        // Retorna el DTO con la información del PMI y el JSON de FHIR
        return new PatientResponseDTO(savedPmi, fhirResponseJson);
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByNationalIdWithFhirData(String nationalId) {
        PatientMasterIndex pmiEntry = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Patient with DNI " + nationalId + " not found in the PMI."));

        String fhirPatientJson = fhirBaseService.getResourceById("Patient", pmiEntry.getFhirId())
                .orElseThrow(() -> new RuntimeException("Resource Patient not found in Aidbox with ID: " + pmiEntry.getFhirId()));

        return new PatientResponseDTO(pmiEntry, fhirPatientJson);
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getAllPatientsWithFhirData() {
        List<PatientMasterIndex> allPmis = pmiRepository.findAll();

        return allPmis.stream().map(pmi -> {
            String fhirPatientJson;
            try {
                // Intentar obtener el recurso FHIR del Aidbox, ver la posibilidad en FHIR de devolver un Json más amigable tipo pretty print
                fhirPatientJson = fhirBaseService.getResourceById("Patient", pmi.getFhirId())
                        .orElse("{\"resourceType\":\"Patient\",\"id\":\"" + pmi.getFhirId() + "\",\"identifier\":[{\"system\":\"error\",\"value\":\"" + pmi.getNationalId() + "\"}],\"name\":[{\"family\":\"Error\",\"given\":[\"Missing FHIR Data\"]}],\"gender\":\"unknown\",\"birthDate\":\"1900-01-01\"}"); // Fallback JSON si no se encuentra
            } catch (Exception e) {
                // Maneja errores si la llamada a Aidbox falla para un paciente específico
                System.err.println("Error fetching FHIR data for patient " + pmi.getNationalId() + " (FHIR ID: " + pmi.getFhirId() + "): " + e.getMessage());
                fhirPatientJson = "{\"resourceType\":\"Patient\",\"id\":\"" + pmi.getFhirId() + "\",\"identifier\":[{\"system\":\"error\",\"value\":\"" + pmi.getNationalId() + "\"}],\"name\":[{\"family\":\"Error\",\"given\":[\"Loading Data Error\"]}],\"gender\":\"unknown\",\"birthDate\":\"1900-01-01\"}";
            }
            return new PatientResponseDTO(pmi, fhirPatientJson);
        }).collect(Collectors.toList());
    }

    /*
    @Transactional
    public PatientResponseDTO updatePatient(String nationalId, String updatedFhirPatientJson) {
        PatientMasterIndex existingPatient = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Patient with DNI " + nationalId + " not found"));

        // Actualizar el recurso FHIR en Aidbox
        String updatedFhirResponseJson = fhirBaseService.updateResource("Patient", existingPatient.getFhirId(), updatedFhirPatientJson);
        // Retornar el DTO actualizado
        return new PatientResponseDTO(existingPatient, updatedFhirResponseJson);
    }
     */

    @Transactional
    public void deletePatient(String nationalId) {
        PatientMasterIndex existingPatient = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Patient with DNI " + nationalId + " not found."));

        // ehrBaseService.deleteEhr(existingPatient.getEhrId());

        pmiRepository.delete(existingPatient);
    }

}