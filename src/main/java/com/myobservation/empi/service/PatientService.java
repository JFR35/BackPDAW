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
import com.myobservation.empi.repository.PatientMasterRepository;
import com.myobservation.empi.repository.PractitionerRepository;
import com.myobservation.empi.repository.VisitMasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    public PatientMasterIndex registerNewPatient(String patientJson, String nationalId) {
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
        pmiEntry.setEhrId(ehrId);

        return pmiRepository.save(pmiEntry);
    }

    @Transactional
    public PatientMasterIndex assignPractitionerToPatient(String patientNationalId, String practitionerNationalId) {
        PatientMasterIndex patientEntry = pmiRepository.findByNationalId(patientNationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + patientNationalId + " no encontrado."));

        PractitionerMasterIndex practitionerEntry = practitionerRepository.findByNationalId(practitionerNationalId)
                .orElseThrow(() -> new RuntimeException("Profesional de la salud con ID " + practitionerNationalId + " no encontrado."));

        patientEntry.setAssignedPractitioner(practitionerEntry);
        return pmiRepository.save(patientEntry);
    }

    @Transactional
    public String createBloodPressureRecord(String nationalId, BloodPressureRequestDTO requestDTO) {
        PatientMasterIndex pmiEntry = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado en el PMI."));

        PractitionerMasterIndex practitioner = pmiEntry.getAssignedPractitioner();

        String compositionId = ehrBaseService.createBloodPressureComposition(requestDTO, pmiEntry.getEhrId().toString());

        Visit visit = new Visit();
        visit.setVisitId(UUID.randomUUID().toString());
        visit.setPatient(pmiEntry);
        visit.setPractitioner(practitioner);
        visit.setCompositionId(compositionId);
        visit.setVisitDate(LocalDateTime.now());
        visitMasterRepository.save(visit);

        return compositionId;
    }

    @Transactional(readOnly = true)
    public List<BloodPressureMeasurementDto> getBloodPressureHistory(String nationalId) {
        PatientMasterIndex pmiEntry = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado en el PMI."));

        // Usar el nuevo método de EhrBaseService
        List<BloodPressureMeasurementDto> measurements = ehrBaseService.queryBloodPressureHistory(pmiEntry.getEhrId().toString());

        // Enriquecer con información del practitioner desde las visitas (si es necesario)
        List<Visit> visits = visitMasterRepository.findByPatient(pmiEntry);
        Map<String, String> compositionToPractitioner = new HashMap<>();
        for (Visit visit : visits) {
            if (visit.getCompositionId() != null && visit.getPractitioner() != null) {
                compositionToPractitioner.put(visit.getCompositionId(), visit.getPractitioner().getName());
            }
        }

        // Nota: No podemos correlacionar directamente las mediciones con las visitas porque la consulta AQL
        // no devuelve el compositionId. Esto requeriría modificar la consulta AQL para incluir el compositionId.
        // Por ahora, dejamos measuredBy como null.

        return measurements;
    }

    public Map<String, String> getPatientData(String nationalId) {
        PatientMasterIndex pmiEntry = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado en el PMI."));

        Map<String, String> data = new HashMap<>();

        String fhirPatientJson = fhirBaseService.getResourceById("Patient", pmiEntry.getFhirId())
                .orElseThrow(() -> new RuntimeException("Recurso Patient no encontrado en Aidbox con ID: " + pmiEntry.getFhirId()));
        data.put("fhirPatient", fhirPatientJson);

        if (pmiEntry.getAssignedPractitioner() != null) {
            Optional<String> practitionerJson = fhirBaseService.getResourceById("Practitioner", pmiEntry.getAssignedPractitioner().getFhirId());
            practitionerJson.ifPresent(s -> data.put("practitioner", s));
        }

        return data;
    }

    public Optional<PatientMasterIndex> getPatientByNationalId(String nationalId) {
        return pmiRepository.findByNationalId(nationalId);
    }

    public List<PatientMasterIndex> getAllPatients() {
        return pmiRepository.findAll();
    }

    @Transactional
    public PatientMasterIndex updatePatient(String nationalId, PatientMasterIndex updatedPatient) {
        PatientMasterIndex existingPatient = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado para actualizar."));

        if (updatedPatient.getFhirId() != null && !updatedPatient.getFhirId().isEmpty()) {
            existingPatient.setFhirId(updatedPatient.getFhirId());
        }
        if (updatedPatient.getEhrId() != null) {
            existingPatient.setEhrId(updatedPatient.getEhrId());
        }
        if (updatedPatient.getAssignedPractitioner() != null) {
            PractitionerMasterIndex newAssignedPractitioner = practitionerRepository.findByNationalId(updatedPatient.getAssignedPractitioner().getNationalId())
                    .orElseThrow(() -> new RuntimeException("Practitioner asignado no encontrado"));
            existingPatient.setAssignedPractitioner(newAssignedPractitioner);
        } else if (existingPatient.getAssignedPractitioner() != null) {
            existingPatient.setAssignedPractitioner(null);
        }

        return pmiRepository.save(existingPatient);
    }

    @Transactional
    public void deletePatient(String nationalId) {
        PatientMasterIndex existingPatient = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado para eliminar."));
        pmiRepository.delete(existingPatient);
    }
}