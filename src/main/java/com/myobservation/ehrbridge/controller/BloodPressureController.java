package com.myobservation.ehrbridge.controller;

import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.myobservation.ehrbridge.service.EhrBaseService;

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
     * Endpoint para crear una nueva medición
     *
     * @param requestDTO Datos de la presión sanguínea definidos en el DTO
     * @param ehrId Crea un ehrId si no hay ninguno definido
     * @return Retorna la composición
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createBloodPressureMeasurement(
            @Valid
            @RequestBody BloodPressureRequestDTO requestDTO,
            @RequestParam(required = false)
            String ehrId) {

        // Validar la requestDTO
        if (requestDTO.getSystolic() == null || requestDTO.getDiastolic() == null) {
            throw new IllegalArgumentException("Systolic and diastolic values are required");
        }
        // Guarda la composición
        String compositionId = ehrBaseService.createBloodPressureComposition(requestDTO, ehrId);
        // Retorna responseDTO con el ID de la composición creada
        Map<String, String> response = new HashMap<>();
        response.put("compositionId", compositionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint para verificar la conexión con Ehrbase
    @GetMapping("/verify-ehrbase")
    public ResponseEntity<String> verifyEhrbaseConnection() {
        if (ehrBaseService.verifyConnection()) {
            return ResponseEntity.ok("Connection to Ehrbase successful!");
        } else {
            return ResponseEntity.status(500).body("Failed to connect to Ehrbase.");
        }
    }

    /**
     * Recuperar una composición almacenada en EHRbase por su ehrID
     * @param ehrId Es el identificador lógico que ehrBase asigna
     * @param compositionId Identificador de la composición
     * @param format Por defecto devuelte FLAT no JSON
     * @return
     */
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