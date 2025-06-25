package com.myobservation.ehrbridge.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Encapsular datos para enviar información estructurada desde y hacia el front
 * Estandariza los datos y asegura que sean correctos antes de persistir, como cualquier DTO
 * Incopora validaciones @NotBlank
 */
public class BloodPressureRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    @NotBlank(message = "Location is required")
    private String location;
    @NotBlank(message = "Composer name is required")
    private String composerName;
    @NotNull(message = "Measurement time is required")
    private LocalDateTime measurementTime;
    // Valores del template
    @NotNull(message = "Systolic value is required")
    private Double systolic;
    @NotNull(message = "Diastolic value is required")
    private Double diastolic;
    @PositiveOrZero(message = "Mean arterial pressure must be positive or zero")
    private Double meanArterialPressure;

    // Getters & Setters
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComposerName() {
        return composerName;
    }

    public void setComposerName(String composerName) {
        this.composerName = composerName;
    }

    public Double getSystolic() {
        return systolic;
    }

    public void setSystolic(Double systolic) {
        this.systolic = systolic;
    }

    public Double getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(Double diastolic) {
        this.diastolic = diastolic;
    }

    public Double getMeanArterialPressure() {
        return meanArterialPressure;
    }

    public void setMeanArterialPressure(Double meanArterialPressure) {
        this.meanArterialPressure = meanArterialPressure;
    }

    public LocalDateTime getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(LocalDateTime measurementTime) {
        this.measurementTime = measurementTime;
    }
}
