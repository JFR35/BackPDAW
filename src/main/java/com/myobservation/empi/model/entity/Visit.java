// src/main/java/com/myobservation/empi/model/entity/Visit.java
package com.myobservation.empi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID; // Importa UUID

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String visitUuid; // UUID para la lógica de negocio y exposición externa

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading para evitar cargar objetos Patient completos
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientMasterIndex patient;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading
    @JoinColumn(name = "practitioner_id", nullable = false)
    private PractitionerMasterIndex practitioner;

    @Column(nullable = false)
    private LocalDateTime visitDate;

    private String bloodPressureCompositionId;

    @PrePersist
    public void generateUuidAndSetDate() {
        if (this.visitUuid == null) {
            this.visitUuid = UUID.randomUUID().toString();
        }
        if (this.visitDate == null) {
            this.visitDate = LocalDateTime.now();
        }
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVisitUuid() { return visitUuid; }
    public void setVisitUuid(String visitUuid) { this.visitUuid = visitUuid; }
    public PatientMasterIndex getPatient() { return patient; }
    public void setPatient(PatientMasterIndex patient) { this.patient = patient; }
    public PractitionerMasterIndex getPractitioner() { return practitioner; }
    public void setPractitioner(PractitionerMasterIndex practitioner) { this.practitioner = practitioner; }
    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }
    public String getBloodPressureCompositionId() { return bloodPressureCompositionId; }
    public void setBloodPressureCompositionId(String bloodPressureCompositionId) { this.bloodPressureCompositionId = bloodPressureCompositionId; }
}