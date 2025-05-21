package com.myobservation.ehr.controller;

import com.myobservation.ehr.model.BloodPressureRequestDTO;
import jakarta.validation.Valid;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myobservation.ehr.service.EhrBaseService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/blood-pressure")
public class BloodPressureController {

    private final EhrBaseService ehrBaseService;

    public BloodPressureController(EhrBaseService ehrBaseService) {
        this.ehrBaseService = ehrBaseService;
    }

    /**
     * Endpoint to create a new blood pressure measurement
     *
     * @param requestDTO Blood pressure data
     * @param ehrId Optional EHR ID, if not provided a new one will be created
     * @return Response with compositionId
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createBloodPressureMeasurement(
            @Valid
            @RequestBody BloodPressureRequestDTO requestDTO,
            @RequestParam(required = false) String ehrId) {

        // Validate the request
        if (requestDTO.getSystolic() == null || requestDTO.getDiastolic() == null) {
            throw new IllegalArgumentException("Systolic and diastolic values are required");
        }

        // Save blood pressure data
        String compositionId = ehrBaseService.createBloodPressureComposition(requestDTO, ehrId);

        // Return response with the composition ID
        Map<String, String> response = new HashMap<>();
        response.put("compositionId", compositionId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Puedes añadir un endpoint para verificar la conexión a Ehrbase si tienes uno
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