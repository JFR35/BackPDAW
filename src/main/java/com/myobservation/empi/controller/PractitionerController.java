// src/main/java/com/myobservation/empi/controller/PractitionerController.java
package com.myobservation.empi.controller;

import com.myobservation.empi.model.dto.PractitionerResponseDTO; // <--- Importa el DTO
import com.myobservation.empi.service.PractitionerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/practitioners") // Nuevo RequestMapping para profesionales
public class PractitionerController {

    private final PractitionerService practitionerService;

    public PractitionerController(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;
    }

    @PostMapping // POST /api/practitioners
    public ResponseEntity<PractitionerResponseDTO> registerPractitioner( // <--- Retorna el DTO
                                                                         @RequestBody String fhirPractitionerJson, // FHIR JSON enviado por el frontend
                                                                         @RequestParam String nationalId) {
        try {
            PractitionerResponseDTO practitionerDto = practitionerService.registerNewPractitioner(fhirPractitionerJson, nationalId);
            return new ResponseEntity<>(practitionerDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // O un DTO de error si lo defines
        }
    }

    @GetMapping("/{nationalId}") // GET /api/practitioners/{nationalId}
    public ResponseEntity<PractitionerResponseDTO> getPractitioner(@PathVariable String nationalId) { // <--- Retorna el DTO
        try {
            PractitionerResponseDTO practitionerDto = practitionerService.getPractitionerByNationalIdWithFhirData(nationalId);
            return ResponseEntity.ok(practitionerDto);
        } catch (RuntimeException e) {
            HttpStatus status = (e.getMessage() != null && e.getMessage().contains("no encontrado")) ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping // GET /api/practitioners (para obtener la lista)
    public ResponseEntity<List<PractitionerResponseDTO>> getAllPractitioners() { // <--- Retorna una lista de DTOs
        try {
            List<PractitionerResponseDTO> practitioners = practitionerService.getAllPractitionersWithFhirData();
            return ResponseEntity.ok(practitioners);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /*
    @PutMapping("/{nationalId}") // PUT /api/practitioners/{nationalId}
    public ResponseEntity<PractitionerResponseDTO> updatePractitioner( // <--- Retorna el DTO
                                                                       @PathVariable String nationalId,
                                                                       @RequestBody String updatedFhirPractitionerJson) { // <--- Espera el JSON completo de FHIR para actualizar
        try {
            PractitionerResponseDTO practitioner = practitionerService.updatePractitioner(nationalId, updatedFhirPractitionerJson);
            return ResponseEntity.ok(practitioner);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    /*
     */
    @DeleteMapping("/{nationalId}") // DELETE /api/practitioners/{nationalId}
    public ResponseEntity<Void> deletePractitioner(@PathVariable String nationalId) {
        try {
            practitionerService.deletePractitioner(nationalId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}