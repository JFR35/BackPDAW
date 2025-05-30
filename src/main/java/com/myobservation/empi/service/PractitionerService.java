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
            throw new RuntimeException("Practitioner with DNI " + nationalId + " already exists.");
        }

        // Almacenar el recurso Practitioner en Aidbox
        String fhirResponseJson = fhirBaseService.storeResource("Practitioner", practitionerJson);
        String fhirId;
        String name;
        String specialty;

        try {
            JsonNode rootNode = objectMapper.readTree(fhirResponseJson);
            fhirId = rootNode.path("id").asText();
            // Es crucial que esta lógica de extracción de 'name' y 'specialty' sea robusta y coincida con el FHIR JSON que envías/recibes.
            // Si 'name' puede ser un array vacío o el 'given' o 'family' no existen, esto podría lanzar un NullPointerException.
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
            throw new RuntimeException("Error response parsing from Aidbox to obtain FHIR ID and profesional data: " + e.getMessage(), e);
        }

        PractitionerMasterIndex pmiEntry = new PractitionerMasterIndex();
        pmiEntry.setNationalId(nationalId);
        pmiEntry.setFhirId(fhirId);
        pmiEntry.setName(name);
        pmiEntry.setSpecialty(specialty);

        PractitionerMasterIndex savedPmi = practitionerRepository.save(pmiEntry);

        return new PractitionerResponseDTO(savedPmi, fhirResponseJson);
    }

    @Transactional(readOnly = true) // Solo lectura probar en otros servicios para mayor robustez
    public PractitionerResponseDTO getPractitionerByNationalIdWithFhirData(String nationalId) {
        PractitionerMasterIndex pmiEntry = practitionerRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("Practitioner with DNI " + nationalId + " not found in the PMI."));

        String fhirPractitionerJson = fhirBaseService.getResourceById("Practitioner", pmiEntry.getFhirId())
                .orElseThrow(() -> new RuntimeException("Resource Practitioner not found in Aidbox with ID: " + pmiEntry.getFhirId()));

        return new PractitionerResponseDTO(pmiEntry, fhirPractitionerJson);
    }

    @Transactional(readOnly = true)
    public List<PractitionerResponseDTO> getAllPractitionersWithFhirData() {
        List<PractitionerMasterIndex> allPmis = practitionerRepository.findAll();

        return allPmis.stream().map(pmi -> {
            String fhirPractitionerJson;
            try { // El parseo es en bruto buscar opcion de pretty print
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

        // fhirBaseService.deleteResource("Practitioner", existingPractitioner.getFhirId());

        practitionerRepository.delete(existingPractitioner);
    }

    //     Optional<PractitionerMasterIndex> findByNationalId(String nationalId);
    //     Optional<PractitionerMasterIndex> findByFhirId(String fhirId);
    // }
}