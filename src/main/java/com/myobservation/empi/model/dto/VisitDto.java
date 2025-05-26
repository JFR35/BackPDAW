package com.myobservation.empi.model.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class VisitDto {

    @NotNull
    private String visitId;

    @NotNull
    private LocalDateTime visitDate;

    private String practitionerName;
    private String compositionId;

    // Constructor, getters, and setters
    public VisitDto(String visitId, LocalDateTime visitDate, String practitionerName, String compositionId) {
        this.visitId = visitId;
        this.visitDate = visitDate;
        this.practitionerName = practitionerName;
        this.compositionId = compositionId;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public String getPractitionerName() {
        return practitionerName;
    }

    public void setPractitionerName(String practitionerName) {
        this.practitionerName = practitionerName;
    }

    public String getCompositionId() {
        return compositionId;
    }

    public void setCompositionId(String compositionId) {
        this.compositionId = compositionId;
    }
}