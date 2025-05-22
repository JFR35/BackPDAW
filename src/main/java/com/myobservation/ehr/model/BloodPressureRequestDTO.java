package com.myobservation.ehr.model;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

public class BloodPressureRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // Patient information
    private String patientId;
    // Measurement context
    private String location;
    private String composerName;
    private LocalDateTime measurementTime;

    // Blood pressure values
    @NotNull(message = "Systolic value is required")
    private Double systolic;
    @NotNull(message = "Diastolic value is required")
    private Double diastolic;
    private Double meanArterialPressure;

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
