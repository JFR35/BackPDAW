// src/main/java/com/myobservation/empi/controller/PatientController.java
package com.myobservation.empi.controller;

import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.empi.model.dto.BloodPressureMeasurementDto;
import com.myobservation.empi.model.dto.PatientResponseDTO; // <--- Importa el DTO
import com.myobservation.empi.model.entity.PatientMasterIndex;
import com.myobservation.empi.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map; // Para errores o respuestas específicas
import java.util.Optional; // Para respuestas de Optional

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping // POST /api/patients
    public ResponseEntity<PatientResponseDTO> registerPatient( // <--- Retorna el DTO
                                                               @RequestBody String fhirPatientJson, // FHIR JSON enviado por el frontend
                                                               @RequestParam String nationalId) {
        try {
            PatientResponseDTO patientDto = patientService.registerNewPatient(fhirPatientJson, nationalId);
            return new ResponseEntity<>(patientDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // O un DTO de error si lo defines
        }
    }

    @GetMapping("/{nationalId}") // GET /api/patients/{nationalId}
    public ResponseEntity<PatientResponseDTO> getPatient(@PathVariable String nationalId) { // <--- Retorna el DTO
        try {
            PatientResponseDTO patientDto = patientService.getPatientByNationalIdWithFhirData(nationalId); // <--- Usa el nuevo método del servicio
            return ResponseEntity.ok(patientDto);
        } catch (RuntimeException e) {
            HttpStatus status = (e.getMessage() != null && e.getMessage().contains("no encontrado")) ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping // GET /api/patients (para obtener la lista)
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() { // <--- Retorna una lista de DTOs
        try {
            List<PatientResponseDTO> patients = patientService.getAllPatientsWithFhirData(); // <--- Usa el nuevo método del servicio
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    /*
    @PutMapping("/{nationalId}") // PUT /api/patients/{nationalId}
    public ResponseEntity<PatientResponseDTO> updatePatient( // <--- Retorna el DTO
                                                             @PathVariable String nationalId,
                                                             @RequestBody String updatedFhirPatientJson) { // <--- Espera el JSON completo de FHIR para actualizar
        try {
            // Asume que tu updatePatient en el servicio actualiza en Aidbox y devuelve el DTO
            PatientResponseDTO patient = patientService.updatePatient(nationalId, updatedFhirPatientJson);
            return ResponseEntity.ok(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

     */
    /*
    @DeleteMapping("/{nationalId}") // DELETE /api/patients/{nationalId}
    public ResponseEntity<Void> deletePatient(@PathVariable String nationalId) {
        try {
            patientService.deletePatient(nationalId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

     */
    /*
    // --- Mantenemos los demás endpoints de relaciones/observaciones si los necesitas ---
    @PostMapping("/{nationalId}/blood-pressure")
    public ResponseEntity<Map<String, String>> addBloodPressureMeasurement(
            @PathVariable String nationalId,
            @Valid @RequestBody BloodPressureRequestDTO requestDTO) {
        try {
            String compositionId = patientService.createBloodPressureRecord(nationalId, requestDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Medición de presión sanguínea guardada exitosamente.");
            response.put("compositionId", compositionId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar medición de presión sanguínea: " + e.getMessage()));
        }
    }

     */
    /*
    @PutMapping("/{patientNationalId}/assign-practitioner/{practitionerNationalId}")
    public ResponseEntity<Map<String, String>> assignPractitionerToPatient(
            @PathVariable String patientNationalId,
            @PathVariable String practitionerNationalId) {
        try {
            PatientMasterIndex pmi = patientService.assignPractitionerToPatient(patientNationalId, practitionerNationalId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profesional de la salud asignado exitosamente al paciente.");
            response.put("patientNationalId", pmi.getNationalId());
            if (pmi.getAssignedPractitioner() != null) {
                response.put("assignedPractitionerId", pmi.getAssignedPractitioner().getNationalId());
                response.put("assignedPractitionerFhirId", pmi.getAssignedPractitioner().getFhirId());
            } else {
                response.put("assignedPractitionerId", "N/A");
                response.put("assignedPractitionerFhirId", "N/A");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            HttpStatus status = (e.getMessage() != null && e.getMessage().contains("no encontrado")) ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status)
                    .body(Map.of("error", "Error al asignar profesional de la salud: " + e.getMessage()));
        }
    }

     */
    /*
    @GetMapping("/{nationalId}/blood-pressure-history")
    public ResponseEntity<List<BloodPressureMeasurementDto>> getBloodPressureHistory(@PathVariable String nationalId) {
        try {
            List<BloodPressureMeasurementDto> history = patientService.getBloodPressureHistory(nationalId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

     */
}