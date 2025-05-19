package com.myobservation.ehr.controller;

import com.myobservation.ehr.mapper.BloodPressureMapper;
import com.myobservation.ehr.model.definition.PresionSanguineaComposition;
import com.myobservation.ehr.model.dto.BloodPressureDto;
import com.myobservation.ehr.service.BloodPressureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/blood-pressure")
public class BloodPressureController {

    private final BloodPressureService bloodPressureService;

    public BloodPressureController(BloodPressureService bloodPressureService) {
        this.bloodPressureService = bloodPressureService;
    }

    @PostMapping("/{ehrId}")
    public ResponseEntity<String> createComposition(
            @PathVariable UUID ehrId,
            @Valid @RequestBody BloodPressureDto dto) {
        try {
            PresionSanguineaComposition composition = BloodPressureMapper.toComposition(dto);
            String compositionId = bloodPressureService.createComposition(ehrId, composition);
            return ResponseEntity.ok(compositionId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating composition: " + e.getMessage());
        }
    }

    @PostMapping("/ehr")
    public ResponseEntity<String> createEhr() {
        try {
            UUID ehrId = bloodPressureService.createEhr();
            return ResponseEntity.ok(ehrId.toString());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating EHR: " + e.getMessage());
        }
    }
}