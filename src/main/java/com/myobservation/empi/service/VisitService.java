// src/main/java/com/myobservation/empi/service/VisitService.java
package com.myobservation.empi.service;

import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.ehrbridge.service.EhrBaseService;
import com.myobservation.empi.model.dto.BloodPressureMeasurementDto;
import com.myobservation.empi.model.dto.VisitRequestDTO;
import com.myobservation.empi.model.dto.VisitResponseDTO;
import com.myobservation.empi.model.entity.PatientMasterIndex;
import com.myobservation.empi.model.entity.PractitionerMasterIndex;
import com.myobservation.empi.model.entity.Visit;
import com.myobservation.empi.repository.PatientMasterRepository;
import com.myobservation.empi.repository.PractitionerRepository;
import com.myobservation.empi.repository.VisitMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VisitService {
    private static final Logger logger = LoggerFactory.getLogger(VisitService.class);

    private final EhrBaseService ehrBaseService;
    private final PatientMasterRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final VisitMasterRepository visitRepository;

    public VisitService(EhrBaseService ehrBaseService,
                        PatientMasterRepository patientRepository,
                        PractitionerRepository practitionerRepository,
                        VisitMasterRepository visitRepository) {
        this.ehrBaseService = ehrBaseService;
        this.patientRepository = patientRepository;
        this.practitionerRepository = practitionerRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public VisitResponseDTO createVisitWithBloodPressure(VisitRequestDTO requestDTO) {
        logger.debug("Attempting to create visit for patient: {}, practitioner: {}",
                requestDTO.getPatientNationalId(), requestDTO.getPractitionerNationalId());

        // Validar y obtener PatientMasterIndex
        PatientMasterIndex patient = patientRepository.findByNationalId(requestDTO.getPatientNationalId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found DNI: " + requestDTO.getPatientNationalId()));

        // Validar y obtener PractitionerMasterIndex
        PractitionerMasterIndex practitioner = practitionerRepository.findByNationalId(requestDTO.getPractitionerNationalId())
                .orElseThrow(() -> new IllegalArgumentException("Profesional not found with DNI: " + requestDTO.getPractitionerNationalId()));

        // Crear la entidad Visit inicialmente sin compositionId
        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setPractitioner(practitioner);
        visit.setVisitDate(requestDTO.getVisitDate() != null ? requestDTO.getVisitDate().toLocalDateTime() : LocalDateTime.now());
        // El visitUuid se genera en el método @PrePersist en la entidad Visit
        Visit savedVisit = visitRepository.save(visit); //

        logger.debug("Visit created in EMPI DB with UUID: {}", savedVisit.getVisitUuid());

        String compositionId = null;
        if (requestDTO.getBloodPressureMeasurement() != null) {
            // Preparar y enviar la medición a EHRbase
            BloodPressureMeasurementDto bpDto = requestDTO.getBloodPressureMeasurement();

            BloodPressureRequestDTO ehrBaseBpRequest = new BloodPressureRequestDTO();
            ehrBaseBpRequest.setPatientId(patient.getNationalId()); // Usa el DNI/NIE del paciente
            ehrBaseBpRequest.setSystolic(bpDto.getSystolicMagnitude());
            ehrBaseBpRequest.setDiastolic(bpDto.getDiastolicMagnitude());
            ehrBaseBpRequest.setLocation(bpDto.getLocation());
            ehrBaseBpRequest.setComposerName(bpDto.getMeasuredBy());
            ehrBaseBpRequest.setMeasurementTime(bpDto.getDate() != null ? bpDto.getDate().toLocalDateTime() : savedVisit.getVisitDate());

            // Asegurar que el EHR del paciente ya exista en EHRbase
            // Es mejor no crear el EHR aquí, ya que el EHR debería ser parte del ciclo de vida del paciente
            // Cuando un paciente se registra en EMPI, se crea su EHR.
            // Aquí, simplemente obtenemos el EHR ID del PatientMasterIndex
            if (patient.getEhrId() == null) {
                // Esto no debería pasar si el flujo de registro de paciente crea el EHR.
                logger.warn("Patient {} does not have an EHR ID in EMPI. Attempting to create one.", patient.getNationalId());
                UUID newEhrId = ehrBaseService.createPatientEhr(patient.getNationalId());
                patient.setEhrId(newEhrId.toString());
                patientRepository.save(patient); // Actualiza el paciente con el EHR ID
                logger.info("New EHR created for patient {}: {}", patient.getNationalId(), newEhrId);
            }

            // Crear la composición en EHRbase
            compositionId = ehrBaseService.createBloodPressureComposition(ehrBaseBpRequest, patient.getEhrId());
            logger.debug("Blood Pressure Composition created in EHRbase with ID: {}", compositionId);

            // Actualizar la entidad Visit con el compositionId
            savedVisit.setBloodPressureCompositionId(compositionId);
            visitRepository.save(savedVisit);
            logger.debug("Visit updated with Blood Pressure Composition ID: {}", compositionId);
        } else {
            logger.info("No blood pressure measurement provided for visit UUID: {}", savedVisit.getVisitUuid());
        }

        // 6. Retornar el DTO de respuesta
        return new VisitResponseDTO(savedVisit);
    }

    @Transactional(readOnly = true)
    public VisitResponseDTO getVisitByUuid(String visitUuid) {
        Visit visit = visitRepository.findByVisitUuid(visitUuid)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with UUID: " + visitUuid));
        return new VisitResponseDTO(visit);
    }

    @Transactional(readOnly = true)
    public List<VisitResponseDTO> getVisitsByPatientNationalId(String patientNationalId) {
        PatientMasterIndex patient = patientRepository.findByNationalId(patientNationalId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with DNI: " + patientNationalId));

        List<Visit> visits = visitRepository.findByPatient(patient);
        return visits.stream()
                .map(VisitResponseDTO::new)
                .collect(Collectors.toList());
    }
}