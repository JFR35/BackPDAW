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
            throw new RuntimeException("Paciente con DNI/NIE " + nationalId + " ya existe.");
        }

        String fhirResponseJson = fhirBaseService.storePatient(patientJson);
        String fhirId;
        try {
            JsonNode rootNode = objectMapper.readTree(fhirResponseJson);
            fhirId = rootNode.path("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta de Aidbox para obtener el FHIR ID", e);
        }

        UUID ehrId = ehrBaseService.createPatientEhr(nationalId);

        PatientMasterIndex pmiEntry = new PatientMasterIndex();
        pmiEntry.setNationalId(nationalId);
        pmiEntry.setFhirId(fhirId);
        pmiEntry.setEhrId(String.valueOf(ehrId));
        // Aquí no se guarda el fhirPatientJson en el PMI porque ya lo tienes en Aidbox

        PatientMasterIndex savedPmi = pmiRepository.save(pmiEntry);

        // Retorna el DTO con la información del PMI y el JSON de FHIR
        return new PatientResponseDTO(savedPmi, fhirResponseJson);
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByNationalIdWithFhirData(String nationalId) {
        PatientMasterIndex pmiEntry = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado en el PMI."));

        String fhirPatientJson = fhirBaseService.getResourceById("Patient", pmiEntry.getFhirId())
                .orElseThrow(() -> new RuntimeException("Recurso Patient no encontrado en Aidbox con ID: " + pmiEntry.getFhirId()));

        return new PatientResponseDTO(pmiEntry, fhirPatientJson);
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getAllPatientsWithFhirData() {
        List<PatientMasterIndex> allPmis = pmiRepository.findAll();

        return allPmis.stream().map(pmi -> {
            String fhirPatientJson;
            try {
                // Intenta obtener el recurso FHIR del Aidbox
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
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado para actualizar."));

        // Actualizar el recurso FHIR en Aidbox
        String updatedFhirResponseJson = fhirBaseService.updateResource("Patient", existingPatient.getFhirId(), updatedFhirPatientJson);

        // Opcional: Si el FHIR ID o el DNI/NIE pudiera cambiar en la actualización de FHIR, lo actualizarías aquí
        // Por ahora, asumimos que nationalId y fhirId son estables.

        // Retornar el DTO actualizado
        return new PatientResponseDTO(existingPatient, updatedFhirResponseJson);
    }
     */

    @Transactional
    public void deletePatient(String nationalId) {
        PatientMasterIndex existingPatient = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado para eliminar."));

        // Opcional: Eliminar el recurso FHIR de Aidbox y el EHR de EHRbase
        // fhirBaseService.deleteResource("Patient", existingPatient.getFhirId());
        // ehrBaseService.deleteEhr(existingPatient.getEhrId());

        pmiRepository.delete(existingPatient);
    }

    // ... (Mantén el resto de tus métodos como assignPractitionerToPatient, createBloodPressureRecord, getBloodPressureHistory)
    // Asegúrate de que los métodos que devuelven PMI directo o Map no se usen para el frontend directamente
    // Si tienes un método getPatientData (Map), considera deprecatearlo o renombrarlo para evitar confusiones.
    // getPatientByNationalId (Optional<PatientMasterIndex>) también es solo para uso interno si ya tienes getPatientByNationalIdWithFhirData
}