package com.myobservation.pmi.controller;

import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.pmi.service.PatientService; // Cambiado a PatientService
import com.myobservation.pmi.entity.PatientMasterIndex;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService; // Inyecta PatientService

    public PatientController(PatientService patientService) { // Constructor actualizado
        this.patientService = patientService;
    }

    // ... (Todos los métodos POST, GET, PUT, DELETE para pacientes y sus operaciones) ...

    @PostMapping
    public ResponseEntity<Map<String, String>> registerPatient(
            @RequestBody String fhirPatientJson,
            @RequestParam String nationalId) {
        try {
            PatientMasterIndex pmi = patientService.registerNewPatient(fhirPatientJson, nationalId); // Usa patientService
            Map<String, String> response = new HashMap<>();
            response.put("message", "Paciente registrado exitosamente.");
            response.put("patientId (national)", pmi.getNationalId());
            response.put("fhirId", pmi.getFhirId());
            response.put("ehrId", pmi.getEhrId().toString());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar paciente: " + e.getMessage()));
        }
    }

    @GetMapping("/{nationalId}")
    public ResponseEntity<Map<String, String>> getPatientCombinedData(@PathVariable String nationalId) {
        try {
            Map<String, String> data = patientService.getPatientData(nationalId); // Usa patientService
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            HttpStatus status = (e.getMessage() != null && e.getMessage().contains("no encontrado")) ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status)
                    .body(Map.of("error", "Error al obtener datos del paciente: " + e.getMessage()));
        }
    }

    @GetMapping("/{nationalId}/pmi")
    public ResponseEntity<PatientMasterIndex> getPatientPmiByNationalId(@PathVariable String nationalId) {
        Optional<PatientMasterIndex> pmi = patientService.getPatientByNationalId(nationalId); // Usa patientService
        return pmi.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PatientMasterIndex>> getAllPatientsPmi() {
        List<PatientMasterIndex> patients = patientService.getAllPatients(); // Usa patientService
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{nationalId}")
    public ResponseEntity<PatientMasterIndex> updatePatient(
            @PathVariable String nationalId,
            @RequestBody PatientMasterIndex updatedPatient) {
        try {
            PatientMasterIndex patient = patientService.updatePatient(nationalId, updatedPatient); // Usa patientService
            return ResponseEntity.ok(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{nationalId}")
    public ResponseEntity<Void> deletePatient(@PathVariable String nationalId) {
        try {
            patientService.deletePatient(nationalId); // Usa patientService
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{nationalId}/blood-pressure")
    public ResponseEntity<Map<String, String>> addBloodPressureMeasurement(
            @PathVariable String nationalId,
            @Valid @RequestBody BloodPressureRequestDTO requestDTO) {
        try {
            String compositionId = patientService.createBloodPressureRecord(nationalId, requestDTO); // Usa patientService
            Map<String, String> response = new HashMap<>();
            response.put("message", "Medición de presión sanguínea guardada exitosamente.");
            response.put("compositionId", compositionId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar medición de presión sanguínea: " + e.getMessage()));
        }
    }

    @PutMapping("/{patientNationalId}/assign-practitioner/{practitionerNationalId}")
    public ResponseEntity<Map<String, String>> assignPractitionerToPatient(
            @PathVariable String patientNationalId,
            @PathVariable String practitionerNationalId) {
        try {
            PatientMasterIndex pmi = patientService.assignPractitionerToPatient(patientNationalId, practitionerNationalId); // Usa patientService
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
}