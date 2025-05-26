package com.myobservation.empi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myobservation.fhirbridge.service.FHIRBaseService;
import com.myobservation.empi.model.entity.PatientMasterIndex;
import com.myobservation.empi.model.entity.PractitionerMasterIndex;
import com.myobservation.empi.repository.PatientMasterRepository; // Necesario para la desasignación
import com.myobservation.empi.repository.PractitionerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PractitionerService { // Cambiado el nombre de la clase

    private final PractitionerRepository practitionerRepository;
    private final PatientMasterRepository patientMasterRepository; // Necesario para la desasignación de pacientes
    private final FHIRBaseService fhirBaseService;
    private final ObjectMapper objectMapper;

    public PractitionerService(PractitionerRepository practitionerRepository,
                               PatientMasterRepository patientMasterRepository, // Inyectar también el repositorio de pacientes
                               FHIRBaseService fhirBaseService,
                               ObjectMapper objectMapper) {
        this.practitionerRepository = practitionerRepository;
        this.patientMasterRepository = patientMasterRepository;
        this.fhirBaseService = fhirBaseService;
        this.objectMapper = objectMapper;
    }



    /**
     * Obtiene un profesional de la salud por su DNI/NIE (ID local).
     */
    public Optional<PractitionerMasterIndex> getPractitionerByNationalId(String nationalId) {
        return practitionerRepository.findByNationalId(nationalId);
    }
    @Transactional
    public PractitionerMasterIndex registerNewPractitioner(String practitionerJson, String nationalId) {
        Optional<PractitionerMasterIndex> existingPractitioner = practitionerRepository.findByNationalId(nationalId);
        if (existingPractitioner.isPresent()) {
            throw new RuntimeException("Profesional de la salud con ID " + nationalId + " ya existe localmente.");
        }

        String fhirResponseJson = fhirBaseService.storeResource("Practitioner", practitionerJson);
        String practitionerFhirId;
        try {
            JsonNode rootNode = objectMapper.readTree(fhirResponseJson);
            practitionerFhirId = rootNode.path("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta de Aidbox para obtener el FHIR ID del Practitioner", e);
        }

        PractitionerMasterIndex practitionerEntry = new PractitionerMasterIndex();
        practitionerEntry.setNationalId(nationalId);
        practitionerEntry.setFhirId(practitionerFhirId);

        try {
            JsonNode rootNode = objectMapper.readTree(practitionerJson);

            // Extraer Nombre (tu código existente)
            JsonNode nameNode = rootNode.path("name").get(0);
            if (nameNode != null) {
                if (nameNode.has("given") && nameNode.path("given").isArray()) {
                    practitionerEntry.setName(nameNode.path("given").get(0).asText() + " " + nameNode.path("family").asText());
                } else if (nameNode.has("family")) {
                    practitionerEntry.setName(nameNode.path("family").asText());
                }
            }

            // --- ¡AÑADIR ESTO PARA LA ESPECIALIDAD! ---
            JsonNode qualificationNode = rootNode.path("qualification");
            if (qualificationNode.isArray() && qualificationNode.size() > 0) {
                JsonNode firstQualification = qualificationNode.get(0);
                JsonNode codeNode = firstQualification.path("code");
                if (codeNode.has("coding") && codeNode.path("coding").isArray() && codeNode.path("coding").size() > 0) {
                    JsonNode codingNode = codeNode.path("coding").get(0);
                    if (codingNode.has("display")) { // Preferir 'display' si existe
                        practitionerEntry.setSpecialty(codingNode.path("display").asText());
                    } else if (codingNode.has("code")) { // Si no hay 'display', usar 'code'
                        practitionerEntry.setSpecialty(codingNode.path("code").asText());
                    }
                }
            }
            // --- FIN AÑADIR ESPECIALIDAD ---

        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo parsear el nombre o la especialidad del Practitioner del JSON: " + e.getMessage());
            // Considera si este error debe impedir el guardado. Para tu caso, quizás no.
        }

        return practitionerRepository.save(practitionerEntry);
    }
    /**
     * Obtiene todos los profesionales de la salud registrados en el PMI.
     */
    public List<PractitionerMasterIndex> getAllPractitioners() {
        return practitionerRepository.findAll();
    }

    /**
     * Actualiza un profesional de la salud existente.
     * Permite actualizar su FHIR ID, nombre o especialidad.
     * @param nationalId DNI/NIE del profesional a actualizar.
     * @param updatedPractitioner Datos del profesional con los campos a actualizar.
     * @return El PractitionerMasterIndex actualizado.
     */
    @Transactional
    public PractitionerMasterIndex updatePractitioner(String nationalId, PractitionerMasterIndex updatedPractitioner) {
        PractitionerMasterIndex existingPractitioner = practitionerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Profesional de la salud con ID " + nationalId + " no encontrado para actualizar."));

        if (updatedPractitioner.getFhirId() != null && !updatedPractitioner.getFhirId().isEmpty()) {
            existingPractitioner.setFhirId(updatedPractitioner.getFhirId());
        }
        if (updatedPractitioner.getName() != null && !updatedPractitioner.getName().isEmpty()) {
            existingPractitioner.setName(updatedPractitioner.getName());
        }
        if (updatedPractitioner.getSpecialty() != null && !updatedPractitioner.getSpecialty().isEmpty()) {
            existingPractitioner.setSpecialty(updatedPractitioner.getSpecialty());
        }

        return practitionerRepository.save(existingPractitioner);
    }

    /**
     * Elimina un profesional de la salud por su DNI/NIE (ID local).
     * Nota: Esto solo elimina la entrada del PMI. No elimina el Practitioner de Aidbox automáticamente.
     */
    @Transactional
    public void deletePractitioner(String nationalId) {
        PractitionerMasterIndex existingPractitioner = practitionerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Profesional de la salud con ID " + nationalId + " no encontrado para eliminar."));

        // Desasignar pacientes antes de eliminar al profesional
        List<PatientMasterIndex> patientsAssignedToThisPractitioner = patientMasterRepository.findByAssignedPractitioner(existingPractitioner);
        for (PatientMasterIndex patient : patientsAssignedToThisPractitioner) {
            patient.setAssignedPractitioner(null);
            patientMasterRepository.save(patient);
        }

        practitionerRepository.delete(existingPractitioner);
    }
}