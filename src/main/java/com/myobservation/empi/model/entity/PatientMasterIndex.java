// src/main/java/com/myobservation/empi/model/entity/PatientMasterIndex.java
package com.myobservation.empi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_master")
public class PatientMasterIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID interno de tu EMPI

    @Column(unique = true, nullable = false)
    private String nationalId; // DNI/NIE del paciente

    @Column(unique = true)
    private String fhirId; // ID del recurso Patient en Aidbox

    @Column(unique = true)
    private String ehrId; // ID del EHR en EHRbase

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    private PractitionerMasterIndex assignedPractitioner;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public String getFhirId() { return fhirId; }
    public void setFhirId(String fhirId) { this.fhirId = fhirId; }
    public String getEhrId() { return ehrId; }
    public void setEhrId(String ehrId) { this.ehrId = ehrId; }
    public PractitionerMasterIndex getAssignedPractitioner() { return assignedPractitioner; }
    public void setAssignedPractitioner(PractitionerMasterIndex assignedPractitioner) { this.assignedPractitioner = assignedPractitioner; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}