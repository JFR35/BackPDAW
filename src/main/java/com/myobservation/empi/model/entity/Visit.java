package com.myobservation.empi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Database primary key

    @Column(unique = true)
    private String visitId; // UUID for business logic

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientMasterIndex patient;

    @ManyToOne
    @JoinColumn(name = "practitioner_id")
    private PractitionerMasterIndex practitioner;

    private LocalDateTime visitDate;

    private String compositionId;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVisitId() { return visitId; }
    public void setVisitId(String visitId) { this.visitId = visitId; }
    public PatientMasterIndex getPatient() { return patient; }
    public void setPatient(PatientMasterIndex patient) { this.patient = patient; }
    public PractitionerMasterIndex getPractitioner() { return practitioner; }
    public void setPractitioner(PractitionerMasterIndex practitioner) { this.practitioner = practitioner; }
    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }
    public String getCompositionId() { return compositionId; }
    public void setCompositionId(String compositionId) { this.compositionId = compositionId; }
}