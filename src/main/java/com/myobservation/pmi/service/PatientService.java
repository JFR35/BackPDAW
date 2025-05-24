package com.myobservation.pmi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.ehrbridge.service.EhrBaseService;
import com.myobservation.fhirbridge.service.FHIRBaseService;
import com.myobservation.pmi.entity.PatientMasterIndex;
import com.myobservation.pmi.entity.PractitionerMasterIndex;
import com.myobservation.pmi.repository.PatientMasterRepository;
import com.myobservation.pmi.repository.PractitionerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService { // Cambiado el nombre de la clase

    private final PatientMasterRepository pmiRepository;
    private final PractitionerRepository practitionerRepository; // Sigue siendo necesaria para la asignación
    private final FHIRBaseService fhirBaseService;
    private final EhrBaseService ehrBaseService;
    private final ObjectMapper objectMapper;

    public PatientService(PatientMasterRepository pmiRepository,
                          PractitionerRepository practitionerRepository, // Mantener la inyección
                          FHIRBaseService fhirBaseService,
                          EhrBaseService ehrBaseService,
                          ObjectMapper objectMapper) {
        this.pmiRepository = pmiRepository;
        this.practitionerRepository = practitionerRepository;
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

    /**
     * Asigna un profesional de la salud existente a un paciente existente.
     *
     * @param patientNationalId      El DNI/NIE del paciente.
     * @param practitionerNationalId El DNI/NIE del profesional de la salud.
     * @return El PatientMasterIndex actualizado.
     */
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

        String practitionerFhirId = null;
        if (pmiEntry.getAssignedPractitioner() != null) {
            practitionerFhirId = pmiEntry.getAssignedPractitioner().getFhirId();
            // Si tu BloodPressureRequestDTO necesita el practitionerFhirId,
            // deberías setearlo en el DTO aquí antes de pasarlo a createBloodPressureComposition
            // requestDTO.setPractitionerFhirId(practitionerFhirId);
        } else {
            System.out.println("Advertencia: No hay profesional de la salud asignado para la composición de presión sanguínea.");
        }

        String compositionId = ehrBaseService.createBloodPressureComposition(requestDTO, pmiEntry.getEhrId().toString());
        return compositionId;
    }

    /**
     * Recupera los datos del paciente en formato JSON (como Strings).
     *
     * @param nationalId DNI/NIE del paciente.
     * @return Un mapa que contiene las cadenas JSON del Patient de FHIR y del Practitioner.
     */
    public Map<String, String> getPatientData(String nationalId) {
        PatientMasterIndex pmiEntry = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado en el PMI."));

        Map<String, String> data = new HashMap<>();

        // 1. Obtener FHIR Patient desde Aidbox
        String fhirPatientJson = fhirBaseService.getResourceById("Patient", pmiEntry.getFhirId())
                .orElseThrow(() -> new RuntimeException("Recurso Patient no encontrado en Aidbox con ID: " + pmiEntry.getFhirId()));
        data.put("fhirPatient", fhirPatientJson);

        // 2. Obtener Practitioner (si existe y está asignado en PMI)
        if (pmiEntry.getAssignedPractitioner() != null) {
            Optional<String> practitionerJson = fhirBaseService.getResourceById("Practitioner", pmiEntry.getAssignedPractitioner().getFhirId());
            practitionerJson.ifPresent(s -> data.put("practitioner", s));
        }

        // 3. Puedes añadir aquí la lógica para obtener las composiciones de EHRbase si lo deseas.
        // Por ejemplo:
        // List<String> ehrCompositions = ehrBaseService.getCompositionsByEhrId(pmiEntry.getEhrId());
        // data.put("ehrCompositions", objectMapper.writeValueAsString(ehrCompositions)); // Convertir lista a JSON String

        return data;
    }

    // --- MÉTODOS CRUD PARA PATIENT ---

    /**
     * Obtiene un paciente por su DNI/NIE.
     */
    public Optional<PatientMasterIndex> getPatientByNationalId(String nationalId) {
        return pmiRepository.findByNationalId(nationalId);
    }

    /**
     * Obtiene todos los pacientes registrados en el PMI.
     */
    public List<PatientMasterIndex> getAllPatients() {
        return pmiRepository.findAll();
    }

    /**
     * Actualiza un paciente existente.
     * Permite actualizar el FHIR ID o el EHR ID, y reasignar el Practitioner.
     * @param nationalId DNI/NIE del paciente a actualizar.
     * @param updatedPatient Datos del paciente con los campos a actualizar.
     * @return El PatientMasterIndex actualizado.
     */
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
        // Asignar nuevo practitioner si se proporciona en updatedPatient y es diferente
        if (updatedPatient.getAssignedPractitioner() != null) {
            PractitionerMasterIndex newAssignedPractitioner = practitionerRepository.findByNationalId(updatedPatient.getAssignedPractitioner().getNationalId())
                    .orElseThrow(() -> new RuntimeException("Practitioner asignado no encontrado"));
            existingPatient.setAssignedPractitioner(newAssignedPractitioner);
        } else {
            // Si assignedPractitioner es null en updatedPatient y era null antes, no hacemos nada.
            // Si era asignado y ahora es null, desasignamos.
            if (existingPatient.getAssignedPractitioner() != null) {
                existingPatient.setAssignedPractitioner(null);
            }
        }


        return pmiRepository.save(existingPatient);
    }

    /**
     * Elimina un paciente por su DNI/NIE.
     * Nota: Esto solo elimina la entrada del PMI. No elimina el Patient de Aidbox ni el EHR de EHRbase automáticamente.
     */
    @Transactional
    public void deletePatient(String nationalId) {
        PatientMasterIndex existingPatient = pmiRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI/NIE " + nationalId + " no encontrado para eliminar."));

        pmiRepository.delete(existingPatient);
    }
}