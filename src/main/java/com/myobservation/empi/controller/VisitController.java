package com.myobservation.empi.controller;

import com.myobservation.ehrbridge.service.EhrBaseService;
import com.myobservation.empi.model.entity.Visit;
import com.myobservation.empi.service.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final EhrBaseService ehrBaseService;

    public VisitController(VisitService visitService, EhrBaseService ehrBaseService) {
        this.visitService = visitService;
        this.ehrBaseService = ehrBaseService;
    }

    static class CreateVisitRequest {
        public Long patientId;
        public Long practitionerId;
        public LocalDateTime visitDate;
    }

    @PostMapping
    public ResponseEntity<?> createVisit(@RequestBody CreateVisitRequest request) {
        try {
            Visit newVisit = visitService.createVisit(request.patientId, request.practitionerId, request.visitDate);
            return new ResponseEntity<>(newVisit, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error creating visit", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", e.getMessage()));
        }
    }

    @PutMapping("/{visitLocalId}/composition")
    public ResponseEntity<?> updateVisitCompositionId(@PathVariable Long visitLocalId,
                                                      @RequestBody Map<String, String> body) {
        try {
            String compositionId = body.get("compositionId");
            if (compositionId == null || compositionId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid input", "Composition ID is required"));
            }
            Visit updatedVisit = visitService.updateVisitWithCompositionId(visitLocalId, compositionId);
            return ResponseEntity.ok(updatedVisit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Error updating visit", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", e.getMessage()));
        }
    }

    @GetMapping("/{visitLocalId}")
    public ResponseEntity<Visit> getVisitById(@PathVariable Long visitLocalId) {
        return visitService.getVisitById(visitLocalId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getVisitsByPatient(@PathVariable Long patientId) {
        try {
            List<Visit> visits = visitService.getVisitsByPatient(patientId);
            return ResponseEntity.ok(visits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Error retrieving visits", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", e.getMessage()));
        }
    }

    @DeleteMapping("/{visitLocalId}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Long visitLocalId) {
        try {
            visitService.deleteVisit(visitLocalId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{visitLocalId}/details")
    public ResponseEntity<?> getVisitWithMeasurement(@PathVariable Long visitLocalId) {
        try {
            Visit visit = visitService.getVisitById(visitLocalId)
                    .orElseThrow(() -> new IllegalArgumentException("Visita no encontrada con ID: " + visitLocalId));
            Map<String, Object> response = new HashMap<>();
            response.put("visit", visit);
            if (visit.getCompositionId() != null) {
                if (visit.getPatient().getEhrId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse("Invalid state", "No EHR ID associated with patient"));
                }
                String compositionJson = ehrBaseService.getComposition(visit.getPatient().getEhrId(), visit.getCompositionId());
                response.put("measurement", compositionJson);
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Error retrieving visit", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", e.getMessage()));
        }
    }

    public static class ErrorResponse {
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
}