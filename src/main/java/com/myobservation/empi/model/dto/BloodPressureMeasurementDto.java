package com.myobservation.empi.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.time.OffsetDateTime;

public class BloodPressureMeasurementDto {

    @NotNull(message = "Measurement date is required")
    private OffsetDateTime date;

    @NotNull(message = "Systolic magnitude is required")
    private Double systolicMagnitude;

    @NotEmpty(message = "Systolic unit is required")
    @Pattern(regexp = "mm\\[Hg\\]", message = "Systolic unit must be 'mm[Hg]'")
    private String systolicUnit;

    @NotNull(message = "Diastolic magnitude is required")
    private Double diastolicMagnitude;

    @NotEmpty(message = "Diastolic unit is required")
    @Pattern(regexp = "mm\\[Hg\\]", message = "Diastolic unit must be 'mm[Hg]'")
    private String diastolicUnit;

    @NotEmpty(message = "Location is required")
    private String location;

    @NotEmpty(message = "Measured by is required")
    private String measuredBy;

    @NotNull(message = "Practitioner ID is required")
    private Long practitionerId;

    // Constructors
    public BloodPressureMeasurementDto() {
    }

    public BloodPressureMeasurementDto(OffsetDateTime date, Double systolicMagnitude, String systolicUnit,
                                       Double diastolicMagnitude, String diastolicUnit, String location,
                                       String measuredBy, Long practitionerId) {
        this.date = date;
        this.systolicMagnitude = systolicMagnitude;
        this.systolicUnit = systolicUnit;
        this.diastolicMagnitude = diastolicMagnitude;
        this.diastolicUnit = diastolicUnit;
        this.location = location;
        this.measuredBy = measuredBy;
        this.practitionerId = practitionerId;
    }

    public BloodPressureMeasurementDto(OffsetDateTime measurementDate, Double aDouble, String s, Double aDouble1, String s1, String s2, Object o) {
    }

    // Getters and setters
    public OffsetDateTime getDate() { return date; }
    public void setDate(OffsetDateTime date) { this.date = date; }
    public Double getSystolicMagnitude() { return systolicMagnitude; }
    public void setSystolicMagnitude(Double systolicMagnitude) { this.systolicMagnitude = systolicMagnitude; }
    public String getSystolicUnit() { return systolicUnit; }
    public void setSystolicUnit(String systolicUnit) { this.systolicUnit = systolicUnit; }
    public Double getDiastolicMagnitude() { return diastolicMagnitude; }
    public void setDiastolicMagnitude(Double diastolicMagnitude) { this.diastolicMagnitude = diastolicMagnitude; }
    public String getDiastolicUnit() { return diastolicUnit; }
    public void setDiastolicUnit(String diastolicUnit) { this.diastolicUnit = diastolicUnit; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getMeasuredBy() { return measuredBy; }
    public void setMeasuredBy(String measuredBy) { this.measuredBy = measuredBy; }
    public Long getPractitionerId() { return practitionerId; }
    public void setPractitionerId(Long practitionerId) { this.practitionerId = practitionerId; }
}