package com.myobservation.pmi.controller;

import com.myobservation.ehrbridge.service.EhrBaseService;
import com.myobservation.pmi.model.dto.PMIResponse;
import com.myobservation.pmi.service.PatientMasterService;
import com.myobservation.fhirbridge.service.FHIRBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PatientController {
    private final PatientMasterService patientMasterService;
    private final FHIRBaseService fhirBaseService;
    private final EhrBaseService ehrbaseService;

    public PatientController(PatientMasterService patientMasterService, FHIRBaseService fhirBaseService, EhrBaseService ehrbaseService) {
        this.patientMasterService = patientMasterService;
        this.fhirBaseService = fhirBaseService;
        this.ehrbaseService = ehrbaseService;
    }

}
