// src/main/java/com/myobservation/empi/model/entity/PractitionerMasterIndex.java
package com.myobservation.empi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "practitioner_master")
public class PractitionerMasterIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID interno de tu EMPI

    @Column(unique = true, nullable = false)
    private String nationalId; // DNI/NIE del profesional

    @Column(unique = true, nullable = false)
    private String fhirId; // ID del recurso Practitioner en Aidbox

    private String name; // Nombre completo del profesional
    private String specialty; // Especialidad del profesional

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "assignedPractitioner", fetch = FetchType.LAZY)
    private List<PatientMasterIndex> patients; // Relación con pacientes asignados

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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<PatientMasterIndex> getPatients() { return patients; }
    public void setPatients(List<PatientMasterIndex> patients) { this.patients = patients; }
}