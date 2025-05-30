package com.myobservation.empi.model.dto;

import com.myobservation.empi.model.entity.PractitionerMasterIndex;

public class PractitionerResponseDTO {
    private Long id;
    private String nationalId;
    private String fhirId; // ID del recurso Practitioner en Aidbox
    private String name;
    private String specialty;
    private String fhirPractitionerJson; // Es la representación completa  del JSON del recurso FHIR Practitioner

    // Constructor para facilitar la creación desde PractitionerMasterIndex y el JSON de FHIR
    public PractitionerResponseDTO(PractitionerMasterIndex pmi, String fhirPractitionerJson) {
        this.id = pmi.getId();
        this.nationalId = pmi.getNationalId();
        this.fhirId = pmi.getFhirId();
        this.name = pmi.getName();
        this.specialty = pmi.getSpecialty();
        this.fhirPractitionerJson = fhirPractitionerJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getFhirId() {
        return fhirId;
    }

    public void setFhirId(String fhirId) {
        this.fhirId = fhirId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getFhirPractitionerJson() {
        return fhirPractitionerJson;
    }

    public void setFhirPractitionerJson(String fhirPractitionerJson) {
        this.fhirPractitionerJson = fhirPractitionerJson;
    }
}