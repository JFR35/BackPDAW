// src/main/java/com/myobservation/empi/model/dto/VisitResponseDTO.java
package com.myobservation.empi.model.dto;

import com.myobservation.empi.model.entity.Visit;
import java.time.LocalDateTime;

public class VisitResponseDTO {
    private String visitUuid; // UUID de la visita
    private String patientNationalId;
    private String practitionerNationalId;
    private String practitionerName; // Para mostrar en el frontend
    private LocalDateTime visitDate;
    private String bloodPressureCompositionId; // Si se registró una medición

    // Constructor desde la entidad Visit
    public VisitResponseDTO(Visit visit) {
        this.visitUuid = visit.getVisitUuid();
        this.patientNationalId = visit.getPatient().getNationalId();
        this.practitionerNationalId = visit.getPractitioner().getNationalId();
        this.practitionerName = visit.getPractitioner().getName(); // Asumiendo que PractitionerMasterIndex tiene getName()
        this.visitDate = visit.getVisitDate();
        this.bloodPressureCompositionId = visit.getBloodPressureCompositionId();
    }

    // Getters y Setters
    public String getVisitUuid() { return visitUuid; }
    public void setVisitUuid(String visitUuid) { this.visitUuid = visitUuid; }
    public String getPatientNationalId() { return patientNationalId; }
    public void setPatientNationalId(String patientNationalId) { this.patientNationalId = patientNationalId; }
    public String getPractitionerNationalId() { return practitionerNationalId; }
    public void setPractitionerNationalId(String practitionerNationalId) { this.practitionerNationalId = practitionerNationalId; }
    public String getPractitionerName() { return practitionerName; }
    public void setPractitionerName(String practitionerName) { this.practitionerName = practitionerName; }
    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }
    public String getBloodPressureCompositionId() { return bloodPressureCompositionId; }
    public void setBloodPressureCompositionId(String bloodPressureCompositionId) { this.bloodPressureCompositionId = bloodPressureCompositionId; }
}