package com.myobservation.pmi.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entidad PMI como orquestador de relaciones
 */
@Entity
@Table(name = "patient_master")
public class PatientMasterIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_master_index")
    private Long patientMasterId;

    @Column(name = "internal_id", unique = true, nullable = false)
    private String internalId;

    @Column(name = "national_id", unique = true, nullable = false)
    @Pattern(regexp = "^[0-9]{8}[A-Za-z]$", message = "DNI must contain 8 numbers followed by 1 letter")
    private String nationalId;

    @Column(name = "ehr_id", unique = true, nullable = false)
    private UUID ehrId;

    @Column(name = "fhir_id", unique = true, nullable = false)
    private String fhirId;

    @Column(name = "first_name", nullable = false)
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
    private String lastName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "practitioner_id_fhir")
    private String practitionerIdFhir;

    public PatientMasterIndex() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Constructor con parámetros para la inmutabilidad de la entidad
     * que no debe cambiar los fatos
     * @param internalId
     * @param nationalId
     * @param firstName
     * @param lastName
     */
    public PatientMasterIndex(String internalId, String nationalId, String firstName, String lastName, String practitionerIdFhir) {
        this.internalId = internalId;
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
        this.practitionerIdFhir = practitionerIdFhir;
    }

    public Long getPatientMasterId() { return patientMasterId; }
    public String getInternalId() { return internalId; }
    public String getNationalId() { return nationalId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getPractitionerIdFhir() {return  practitionerIdFhir;}
}
