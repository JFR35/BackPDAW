package com.myobservation.empi.model.dto;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class BloodPressureMeasurementDto {

    @NotNull
    private OffsetDateTime date;

    @NotNull
    private Double systolicMagnitude;

    @NotNull
    private String systolicUnit;

    @NotNull
    private Double diastolicMagnitude;

    @NotNull
    private String diastolicUnit;

    @NotNull
    private String location;

    private String measuredBy;

    public BloodPressureMeasurementDto(OffsetDateTime date, Double systolicMagnitude, String systolicUnit,
                                       Double diastolicMagnitude, String diastolicUnit, String location, String measuredBy) {
        this.date = date;
        this.systolicMagnitude = systolicMagnitude;
        this.systolicUnit = systolicUnit;
        this.diastolicMagnitude = diastolicMagnitude;
        this.diastolicUnit = diastolicUnit;
        this.location = location;
        this.measuredBy = measuredBy; //  si quieres el measuredBy en tu BloodPressureMeasurementDto, necesitarás que la AQL también te devuelva el compositionId para poder buscar en tu tabla Visit y enlazar con el profesional.
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    public Double getSystolicMagnitude() {
        return systolicMagnitude;
    }

    public void setSystolicMagnitude(Double systolicMagnitude) {
        this.systolicMagnitude = systolicMagnitude;
    }

    public String getSystolicUnit() {
        return systolicUnit;
    }

    public void setSystolicUnit(String systolicUnit) {
        this.systolicUnit = systolicUnit;
    }

    public Double getDiastolicMagnitude() {
        return diastolicMagnitude;
    }

    public void setDiastolicMagnitude(Double diastolicMagnitude) {
        this.diastolicMagnitude = diastolicMagnitude;
    }

    public String getDiastolicUnit() {
        return diastolicUnit;
    }

    public void setDiastolicUnit(String diastolicUnit) {
        this.diastolicUnit = diastolicUnit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeasuredBy() {
        return measuredBy;
    }

    public void setMeasuredBy(String measuredBy) {
        this.measuredBy = measuredBy;
    }
}