package com.myobservation.pmi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_master")
public class PatientMasterIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nationalId; // DNI/NIE

    @Column(unique = true)
    private String fhirId; // ID en Aidbox

    @Column(unique = true)
    private UUID ehrId; // ID en EHRbase

    // Muchos pacientes pueden tener un mismo Practitioner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id") // Columna en patient_master que guarda el ID del Practitioner
    private PractitionerMasterIndex assignedPractitioner; // Referencia al Practitioner

    // Metadata
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

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public String getFhirId() { return fhirId; }
    public void setFhirId(String fhirId) { this.fhirId = fhirId; }
    public UUID getEhrId() { return ehrId; }
    public void setEhrId(UUID ehrId) { this.ehrId = ehrId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- NUEVOS GETTERS Y SETTERS PARA LA RELACIÓN ---
    public PractitionerMasterIndex getAssignedPractitioner() {
        return assignedPractitioner;
    }

    public void setAssignedPractitioner(PractitionerMasterIndex assignedPractitioner) {
        this.assignedPractitioner = assignedPractitioner;
    }
}