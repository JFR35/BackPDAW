package com.myobservation.empi.controller;

import com.myobservation.empi.model.dto.BloodPressureMeasurementDto;
import com.myobservation.empi.service.CombinedObservationVisitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/observations")
public class ObservationController {

    private final CombinedObservationVisitService combinedService;

    public ObservationController(CombinedObservationVisitService combinedService) {
        this.combinedService = combinedService;
    }

    @PostMapping("/blood-pressure/{empiPatientId}")
    public ResponseEntity<?> recordBloodPressure(
            @PathVariable String empiPatientId,
            @Valid @RequestBody BloodPressureMeasurementDto dto) {
        try {
            String compositionId = String.valueOf(combinedService.recordObservationWithVisit(empiPatientId, dto));
            return ResponseEntity.ok("Medición registrada con compositionId: " + compositionId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid input", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Internal server error", e.getMessage()));
        }
    }
}

class ErrorResponse {
    private String message;
    private String details;

    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}