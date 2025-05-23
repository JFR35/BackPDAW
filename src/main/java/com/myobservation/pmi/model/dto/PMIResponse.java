package com.myobservation.pmi.model.dto;

import com.nedap.archie.rm.support.identification.UUID;

import java.time.LocalDateTime;
import java.util.List;

public class PMIResponse {

    private Long patientMasterId;
    private String internalId;
    private String nationalId;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private List<String> relatedMeasurements;
    private String practitionerIdFhir;

    public Long getPatientMasterId() {
        return patientMasterId;
    }

    public void setPatientMasterId(Long patientMasterId) {
        this.patientMasterId = patientMasterId;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getRelatedMeasurements() {
        return relatedMeasurements;
    }

    public void setRelatedMeasurements(List<String> relatedMeasurements) {
        this.relatedMeasurements = relatedMeasurements;
    }

    public String getPractitionerIdFhir() {
        return practitionerIdFhir;
    }

    public void setPractitionerIdFhir(String practitionerIdFhir) {
        this.practitionerIdFhir = practitionerIdFhir;
    }
}
