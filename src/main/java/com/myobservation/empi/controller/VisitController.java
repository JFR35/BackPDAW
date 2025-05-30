// src/main/java/com/myobservation/empi/controller/VisitController.java
package com.myobservation.empi.controller;

import com.myobservation.empi.model.dto.VisitRequestDTO;
import com.myobservation.empi.model.dto.VisitResponseDTO;
import com.myobservation.empi.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PostMapping
    public ResponseEntity<VisitResponseDTO> createVisitWithBloodPressure(@Valid @RequestBody VisitRequestDTO requestDTO) {
        try {
            VisitResponseDTO responseDTO = visitService.createVisitWithBloodPressure(requestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Probar a retonar un DTO para ver si da información más completa
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{visitUuid}")
    public ResponseEntity<VisitResponseDTO> getVisitByUuid(@PathVariable String visitUuid) {
        try {
            VisitResponseDTO visit = visitService.getVisitByUuid(visitUuid);
            return ResponseEntity.ok(visit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/patient/{patientNationalId}")
    public ResponseEntity<List<VisitResponseDTO>> getVisitsByPatientNationalId(@PathVariable String patientNationalId) {
        try {
            List<VisitResponseDTO> visits = visitService.getVisitsByPatientNationalId(patientNationalId);
            return ResponseEntity.ok(visits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Faltan endpoints para:
    // - Actualizar una visita
    // - Eliminar una visita considerando que la eliminación de la composición en EHRbase sea efectiva
    // - Obtener solo mediciones de BP si no se desea obtener toda la visita
}