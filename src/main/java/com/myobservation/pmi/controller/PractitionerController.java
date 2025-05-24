package com.myobservation.pmi.controller;

import com.myobservation.pmi.service.PractitionerService; // Cambiado a PractitionerService
import com.myobservation.pmi.entity.PractitionerMasterIndex;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/practitioners")
public class PractitionerController {

    private final PractitionerService practitionerService; // Inyecta PractitionerService

    public PractitionerController(PractitionerService practitionerService) { // Constructor actualizado
        this.practitionerService = practitionerService;
    }

    // ... (Todos los métodos POST, GET, PUT, DELETE para profesionales) ...

    @PostMapping
    public ResponseEntity<Map<String, String>> registerPractitioner(
            @RequestBody String fhirPractitionerJson,
            @RequestParam String nationalId) {
        try {
            PractitionerMasterIndex practitioner = practitionerService.registerNewPractitioner(fhirPractitionerJson, nationalId); // Usa practitionerService
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profesional de la salud registrado exitosamente.");
            response.put("localId", practitioner.getNationalId());
            response.put("fhirId", practitioner.getFhirId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar profesional de la salud: " + e.getMessage()));
        }
    }

    @GetMapping("/{nationalId}")
    public ResponseEntity<PractitionerMasterIndex> getPractitionerByNationalId(@PathVariable String nationalId) {
        Optional<PractitionerMasterIndex> practitioner = practitionerService.getPractitionerByNationalId(nationalId); // Usa practitionerService
        return practitioner.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PractitionerMasterIndex>> getAllPractitioners() {
        List<PractitionerMasterIndex> practitioners = practitionerService.getAllPractitioners(); // Usa practitionerService
        return ResponseEntity.ok(practitioners);
    }

    @PutMapping("/{nationalId}")
    public ResponseEntity<PractitionerMasterIndex> updatePractitioner(
            @PathVariable String nationalId,
            @RequestBody PractitionerMasterIndex updatedPractitioner) {
        try {
            PractitionerMasterIndex practitioner = practitionerService.updatePractitioner(nationalId, updatedPractitioner); // Usa practitionerService
            return ResponseEntity.ok(practitioner);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{nationalId}")
    public ResponseEntity<Void> deletePractitioner(@PathVariable String nationalId) {
        try {
            practitionerService.deletePractitioner(nationalId); // Usa practitionerService
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}