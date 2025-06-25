package com.myobservation.empi.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime; // O LocalDateTime hay que unificar el formato de fechas en todo el sistema

public class VisitRequestDTO {

    @NotEmpty(message = "Patient National ID is required for a visit")
    private String patientNationalId;

    @NotEmpty(message = "Practitioner National ID is required for a visit")
    private String practitionerNationalId;

    // La fecha de la visita es opcional, si no se envía se usa la fecha actual
    private OffsetDateTime visitDate;

    // Aquí anidamos el DTO de la medición de presión arterial
    @Valid // Para que las validaciones internas de BloodPressureMeasurementDto se apliquen
    private BloodPressureMeasurementDto bloodPressureMeasurement;

    // Getters y Setters
    public String getPatientNationalId() { return patientNationalId; }
    public void setPatientNationalId(String patientNationalId) { this.patientNationalId = patientNationalId; }
    public String getPractitionerNationalId() { return practitionerNationalId; }
    public void setPractitionerNationalId(String practitionerNationalId) { this.practitionerNationalId = practitionerNationalId; }
    public OffsetDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(OffsetDateTime visitDate) { this.visitDate = visitDate; }
    public BloodPressureMeasurementDto getBloodPressureMeasurement() { return bloodPressureMeasurement; }
    public void setBloodPressureMeasurement(BloodPressureMeasurementDto bloodPressureMeasurement) { this.bloodPressureMeasurement = bloodPressureMeasurement; }
}