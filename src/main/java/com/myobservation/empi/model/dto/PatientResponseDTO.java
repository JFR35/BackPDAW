package com.myobservation.empi.model.dto;

import com.myobservation.empi.model.entity.PatientMasterIndex;

public class PatientResponseDTO {
    private Long id;
    private String nationalId;
    private String fhirId;
    private String ehrId;
    private String fhirPatientJson; // <-- Devolver completo el FHIR patient Json
    private String assignedPractitionerNationalId; // Todos los datos para mayor depuración
    private String assignedPractitionerFhirId; // De momento se devuelven todos los datos para mayor depuración
    // También se podría incluir un practitioner para la asociación

    // Constructor opcional para convertir desde PatientMasterIndex y fhirPatientJson
    public PatientResponseDTO(PatientMasterIndex pmi, String fhirPatientJson) {
        this.id = pmi.getId();
        this.nationalId = pmi.getNationalId();
        this.fhirId = pmi.getFhirId();
        this.ehrId = pmi.getEhrId();
        this.fhirPatientJson = fhirPatientJson;
        if (pmi.getAssignedPractitioner() != null) {
            this.assignedPractitionerNationalId = pmi.getAssignedPractitioner().getNationalId();
            this.assignedPractitionerFhirId = pmi.getAssignedPractitioner().getFhirId();
        }
    }

    // Getters and Setters
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

    public String getEhrId() {
        return ehrId;
    }

    public void setEhrId(String ehrId) {
        this.ehrId = ehrId;
    }

    public String getFhirPatientJson() {
        return fhirPatientJson;
    }

    public void setFhirPatientJson(String fhirPatientJson) {
        this.fhirPatientJson = fhirPatientJson;
    }

    public String getAssignedPractitionerNationalId() {
        return assignedPractitionerNationalId;
    }

    public void setAssignedPractitionerNationalId(String assignedPractitionerNationalId) {
        this.assignedPractitionerNationalId = assignedPractitionerNationalId;
    }

    public String getAssignedPractitionerFhirId() {
        return assignedPractitionerFhirId;
    }

    public void setAssignedPractitionerFhirId(String assignedPractitionerFhirId) {
        this.assignedPractitionerFhirId = assignedPractitionerFhirId;
    }
}