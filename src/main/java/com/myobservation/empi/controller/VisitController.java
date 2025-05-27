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

    @PostMapping // POST /api/visits
    public ResponseEntity<VisitResponseDTO> createVisitWithBloodPressure(@Valid @RequestBody VisitRequestDTO requestDTO) {
        try {
            VisitResponseDTO responseDTO = visitService.createVisitWithBloodPressure(requestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404 para paciente/profesional no encontrado
                    .body(null); // O un DTO de error más detallado
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{visitUuid}") // GET /api/visits/{visitUuid}
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

    @GetMapping("/patient/{patientNationalId}") // GET /api/visits/patient/{patientNationalId}
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

    // Aquí podrías añadir endpoints para:
    // - Actualizar una visita (quizás su fecha o el profesional asignado)
    // - Eliminar una visita (considerando la eliminación de la composición en EHRbase)
    // - Obtener solo mediciones de BP si no quieres obtener toda la visita
}