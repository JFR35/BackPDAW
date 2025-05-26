package com.myobservation.empi.controller;

import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.empi.service.PatientService;
import com.myobservation.ehrbridge.service.EhrBaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/blood-pressure")
public class EmpiBloodPressureController {

    private final EhrBaseService ehrBaseService;
    private final PatientService patientService;

    public EmpiBloodPressureController(EhrBaseService ehrBaseService, PatientService patientService) {
        this.ehrBaseService = ehrBaseService;
        this.patientService = patientService;
    }

    @PostMapping("/{nationalId}")
    public ResponseEntity<Map<String, String>> createBloodPressureMeasurement(
            @PathVariable String nationalId,
            @Valid @RequestBody BloodPressureRequestDTO requestDTO) {
        if (requestDTO.getSystolic() == null || requestDTO.getDiastolic() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Systolic and diastolic values are required"));
        }

        String compositionId = patientService.createBloodPressureRecord(nationalId, requestDTO);
        Map<String, String> response = new HashMap<>();
        response.put("compositionId", compositionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/verify-ehrbase")
    public ResponseEntity<String> verifyEhrbaseConnection() {
        if (ehrBaseService.verifyConnection()) {
            return ResponseEntity.ok("Connection to Ehrbase successful!");
        } else {
            return ResponseEntity.status(500).body("Failed to connect to Ehrbase.");
        }
    }

    @GetMapping("/{ehrId}/composition/{compositionId}")
    public ResponseEntity<?> getBloodPressureComposition(
            @PathVariable String ehrId,
            @PathVariable String compositionId,
            @RequestParam(value = "format", defaultValue = "FLAT") String format) {
        try {
            if (!"FLAT".equalsIgnoreCase(format)) {
                return ResponseEntity.badRequest().body("Only FLAT format is supported");
            }
            String compositionJson = ehrBaseService.getComposition(ehrId, compositionId);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/openehr.wt.flat.schema+json")
                    .body(compositionJson);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid ehrId or compositionId: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Composition not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving composition: " + e.getMessage());
        }
    }
}