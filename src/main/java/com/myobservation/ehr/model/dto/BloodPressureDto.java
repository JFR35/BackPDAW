package com.myobservation.ehr.model.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class BloodPressureDto {
    @NotNull
    @Min(0)
    private Double systolic;

    @NotNull
    @Min(0)
    private Double diastolic;

    @NotNull
    @Pattern(regexp = "mm\\[Hg\\]", message = "Unit must be mm[Hg]")
    private String unit = "mm[Hg]";

    @NotNull
    private LocalDateTime measurementTime;

    private String position; // e.g., SITTING, STANDING

    // Getters and setters
    public Double getSystolic() { return systolic; }
    public void setSystolic(Double systolic) { this.systolic = systolic; }
    public Double getDiastolic() { return diastolic; }
    public void setDiastolic(Double diastolic) { this.diastolic = diastolic; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public LocalDateTime getMeasurementTime() { return measurementTime; }
    public void setMeasurementTime(LocalDateTime measurementTime) { this.measurementTime = measurementTime; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}