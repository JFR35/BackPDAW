package com.myobservation.empi.service;

import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.ehrbridge.service.EhrBaseService;
import com.myobservation.empi.model.dto.BloodPressureMeasurementDto;
import com.myobservation.empi.model.entity.Visit;
import com.myobservation.empi.model.entity.PatientMasterIndex;
import com.myobservation.empi.repository.PatientMasterRepository;
import com.myobservation.empi.repository.PractitionerRepository;
import com.myobservation.empi.repository.VisitMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CombinedObservationVisitService {
    private static final Logger logger = LoggerFactory.getLogger(CombinedObservationVisitService.class);

    private final EhrBaseService ehrBaseService;
    private final VisitService visitService;
    private final PatientMasterRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final VisitMasterRepository visitRepository;

    public CombinedObservationVisitService(EhrBaseService ehrBaseService, VisitService visitService,
                                           PatientMasterRepository patientRepository, PractitionerRepository practitionerRepository,
                                           VisitMasterRepository visitRepository) {
        this.ehrBaseService = ehrBaseService;
        this.visitService = visitService;
        this.patientRepository = patientRepository;
        this.practitionerRepository = practitionerRepository;
        this.visitRepository = visitRepository;
    }

    public static class ObservationResponse {
        private String compositionId;
        private Long visitLocalId;

        public ObservationResponse(String compositionId, Long visitLocalId) {
            this.compositionId = compositionId;
            this.visitLocalId = visitLocalId;
        }

        public String getCompositionId() { return compositionId; }
        public void setCompositionId(String compositionId) { this.compositionId = compositionId; }
        public Long getVisitLocalId() { return visitLocalId; }
        public void setVisitLocalId(Long visitLocalId) { this.visitLocalId = visitLocalId; }
    }

    @Transactional
    public ObservationResponse recordObservationWithVisit(String empiPatientId, BloodPressureMeasurementDto dto) {
        logger.debug("Processing blood pressure for patientId: {}, practitionerId: {}", empiPatientId, dto.getPractitionerId());

        // Validate patient
        PatientMasterIndex patient = patientRepository.findByNationalId(empiPatientId)
                .orElseThrow(() -> {
                    logger.error("Patient not found with DNI: {}", empiPatientId);
                    return new IllegalArgumentException("Paciente no encontrado con DNI: " + empiPatientId);
                });

        // Validate practitioner
        if (!practitionerRepository.existsById(dto.getPractitionerId())) {
            logger.error("Practitioner not found with ID: {}", dto.getPractitionerId());
            throw new IllegalArgumentException("Profesional no encontrado con ID: " + dto.getPractitionerId());
        }

        // Convert OffsetDateTime to LocalDateTime
        LocalDateTime visitDate = dto.getDate() != null ? dto.getDate().toLocalDateTime() : LocalDateTime.now();
        logger.debug("Converted visit date: {}", visitDate);

        // Create visit
        Visit visit = visitService.createVisit(patient.getId(), dto.getPractitionerId(), visitDate);
        logger.debug("Created visit with ID: {}, visitId: {}", visit.getId(), visit.getVisitId());

        // Prepare EHRbase DTO
        BloodPressureRequestDTO requestDTO = new BloodPressureRequestDTO();
        requestDTO.setPatientId(empiPatientId);
        requestDTO.setSystolic(dto.getSystolicMagnitude());
        requestDTO.setDiastolic(dto.getDiastolicMagnitude());
        requestDTO.setLocation(dto.getLocation());
        requestDTO.setComposerName(dto.getMeasuredBy());
        requestDTO.setMeasurementTime(dto.getDate().toLocalDateTime());

        // Create EHR and save EHR ID
        UUID ehrId = ehrBaseService.createPatientEhr(empiPatientId);
        logger.debug("Created EHR with ID: {}", ehrId);
        patient.setEhrId(ehrId.toString());
        patientRepository.save(patient); // Save EHR ID to patient
        String compositionId = ehrBaseService.createBloodPressureComposition(requestDTO, ehrId.toString());
        logger.debug("Created composition with ID: {}", compositionId);

        // Save visit with compositionId
        visit.setCompositionId(compositionId);
        visitRepository.save(visit);
        logger.debug("Updated visit with compositionId: {}", compositionId);

        return new ObservationResponse(compositionId, visit.getId());
    }
}