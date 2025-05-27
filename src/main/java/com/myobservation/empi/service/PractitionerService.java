// src/main/java/com/myobservation/empi/service/PractitionerService.java
package com.myobservation.empi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myobservation.fhirbridge.service.FHIRBaseService; // Asume que tienes este servicio
import com.myobservation.empi.model.entity.PractitionerMasterIndex;
import com.myobservation.empi.model.dto.PractitionerResponseDTO; // <--- Importa el DTO
import com.myobservation.empi.repository.PractitionerRepository; // Asume que tienes este repositorio
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PractitionerService {

    private final PractitionerRepository practitionerRepository;
    private final FHIRBaseService fhirBaseService;
    private final ObjectMapper objectMapper; // Para parsear JSON si es necesario

    public PractitionerService(PractitionerRepository practitionerRepository,
                               FHIRBaseService fhirBaseService,
                               ObjectMapper objectMapper) {
        this.practitionerRepository = practitionerRepository;
        this.fhirBaseService = fhirBaseService;
        this.objectMapper = objectMapper;
    }
    @Transactional
    public PractitionerResponseDTO registerNewPractitioner(String practitionerJson, String nationalId) {
        Optional<PractitionerMasterIndex> existingPmi = practitionerRepository.findByNationalId(nationalId);
        if (existingPmi.isPresent()) {
            throw new RuntimeException("Profesional con DNI/NIE " + nationalId + " ya existe.");
        }

        // Almacenar el recurso Practitioner en Aidbox
        // --- CAMBIO AQUÍ: Añade "Practitioner" como primer argumento ---
        String fhirResponseJson = fhirBaseService.storeResource("Practitioner", practitionerJson);
        String fhirId;
        String name;
        String specialty;

        try {
            JsonNode rootNode = objectMapper.readTree(fhirResponseJson);
            fhirId = rootNode.path("id").asText();
            // Ejemplo de cómo extraer nombre/especialidad del JSON FHIR (ajusta según tu estructura FHIR Practitioner)
            // Es crucial que esta lógica de extracción de 'name' y 'specialty' sea robusta y coincida con el FHIR JSON que envías/recibes.
            // Si 'name' puede ser un array vacío o el 'given' o 'family' no existen, esto podría lanzar un NullPointerException.
            // Considera usar Optional o comprobar si los nodos existen antes de llamar a .asText().
            JsonNode nameNode = rootNode.path("name");
            if (nameNode.isArray() && nameNode.size() > 0) {
                JsonNode firstGivenName = nameNode.path(0).path("given").path(0);
                JsonNode familyName = nameNode.path(0).path("family");
                name = (firstGivenName.isTextual() ? firstGivenName.asText() : "") + " " + (familyName.isTextual() ? familyName.asText() : "");
            } else {
                name = "Unknown Name"; // Valor por defecto si no se puede extraer
            }

            JsonNode qualificationNode = rootNode.path("qualification");
            if (qualificationNode.isArray() && qualificationNode.size() > 0) {
                JsonNode specialtyDisplay = qualificationNode.path(0).path("code").path("coding").path(0).path("display");
                specialty = specialtyDisplay.isTextual() ? specialtyDisplay.asText() : "Unknown Specialty";
            } else {
                specialty = "Unknown Specialty"; // Valor por defecto
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta de Aidbox para obtener el FHIR ID/datos del profesional: " + e.getMessage(), e);
        }

        PractitionerMasterIndex pmiEntry = new PractitionerMasterIndex();
        pmiEntry.setNationalId(nationalId);
        pmiEntry.setFhirId(fhirId);
        pmiEntry.setName(name); // Guarda el nombre extraído
        pmiEntry.setSpecialty(specialty); // Guarda la especialidad extraída

        PractitionerMasterIndex savedPmi = practitionerRepository.save(pmiEntry);

        return new PractitionerResponseDTO(savedPmi, fhirResponseJson);
    }

    @Transactional(readOnly = true)
    public PractitionerResponseDTO getPractitionerByNationalIdWithFhirData(String nationalId) {
        PractitionerMasterIndex pmiEntry = practitionerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Profesional con DNI/NIE " + nationalId + " no encontrado en el PMI."));

        String fhirPractitionerJson = fhirBaseService.getResourceById("Practitioner", pmiEntry.getFhirId())
                .orElseThrow(() -> new RuntimeException("Recurso Practitioner no encontrado en Aidbox con ID: " + pmiEntry.getFhirId()));

        return new PractitionerResponseDTO(pmiEntry, fhirPractitionerJson);
    }

    @Transactional(readOnly = true)
    public List<PractitionerResponseDTO> getAllPractitionersWithFhirData() {
        List<PractitionerMasterIndex> allPmis = practitionerRepository.findAll();

        return allPmis.stream().map(pmi -> {
            String fhirPractitionerJson;
            try {
                fhirPractitionerJson = fhirBaseService.getResourceById("Practitioner", pmi.getFhirId())
                        .orElse("{\"resourceType\":\"Practitioner\",\"id\":\"" + pmi.getFhirId() + "\",\"identifier\":[{\"system\":\"error\",\"value\":\"" + pmi.getNationalId() + "\"}],\"name\":[{\"family\":\"Error\",\"given\":[\"Missing FHIR Data\"]}],\"gender\":\"unknown\"}"); // Fallback JSON
            } catch (Exception e) {
                System.err.println("Error fetching FHIR data for practitioner " + pmi.getNationalId() + " (FHIR ID: " + pmi.getFhirId() + "): " + e.getMessage());
                fhirPractitionerJson = "{\"resourceType\":\"Practitioner\",\"id\":\"" + pmi.getFhirId() + "\",\"identifier\":[{\"system\":\"error\",\"value\":\"" + pmi.getNationalId() + "\"}],\"name\":[{\"family\":\"Error\",\"given\":[\"Loading Data Error\"]}],\"gender\":\"unknown\"}";
            }
            return new PractitionerResponseDTO(pmi, fhirPractitionerJson);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deletePractitioner(String nationalId) {
        PractitionerMasterIndex existingPractitioner = practitionerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Profesional con DNI/NIE " + nationalId + " no encontrado para eliminar."));

        // Opcional: Eliminar el recurso FHIR de Aidbox
        // fhirBaseService.deleteResource("Practitioner", existingPractitioner.getFhirId());

        practitionerRepository.delete(existingPractitioner);
    }

    // Necesitarás un PractitionerRepository similar a PatientMasterRepository
    // public interface PractitionerRepository extends JpaRepository<PractitionerMasterIndex, Long> {
    //     Optional<PractitionerMasterIndex> findByNationalId(String nationalId);
    //     Optional<PractitionerMasterIndex> findByFhirId(String fhirId);
    // }
}