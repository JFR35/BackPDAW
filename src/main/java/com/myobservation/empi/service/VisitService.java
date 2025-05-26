package com.myobservation.empi.service;

import com.myobservation.empi.model.entity.Visit;
import com.myobservation.empi.model.entity.PatientMasterIndex;
import com.myobservation.empi.model.entity.PractitionerMasterIndex;
import com.myobservation.empi.repository.PatientMasterRepository;
import com.myobservation.empi.repository.PractitionerRepository;
import com.myobservation.empi.repository.VisitMasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VisitService {

    private final VisitMasterRepository visitRepository;
    private final PatientMasterRepository patientRepository;
    private final PractitionerRepository practitionerRepository;

    public VisitService(VisitMasterRepository visitRepository, PatientMasterRepository patientRepository,
                        PractitionerRepository practitionerRepository) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.practitionerRepository = practitionerRepository;
    }

    @Transactional
    public Visit createVisit(Long patientId, Long practitionerId, LocalDateTime visitDate) {
        PatientMasterIndex patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + patientId));

        PractitionerMasterIndex practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado con ID: " + practitionerId));

        Visit visit = new Visit();
        visit.setVisitId(UUID.randomUUID().toString());
        visit.setPatient(patient);
        visit.setPractitioner(practitioner);
        visit.setVisitDate(visitDate);

        return visitRepository.save(visit);
    }

    @Transactional
    public Visit updateVisitWithCompositionId(Long visitLocalId, String compositionId) {
        Visit visit = visitRepository.findById(visitLocalId)
                .orElseThrow(() -> new IllegalArgumentException("Visita no encontrada con ID: " + visitLocalId));
        visit.setCompositionId(compositionId);
        return visitRepository.save(visit);
    }

    public Optional<Visit> getVisitById(Long visitLocalId) {
        return visitRepository.findById(visitLocalId);
    }

    public List<Visit> getVisitsByPatient(Long patientId) {
        PatientMasterIndex patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + patientId));
        return visitRepository.findByPatient(patient);
    }

    @Transactional
    public void deleteVisit(Long visitLocalId) {
        if (!visitRepository.existsById(visitLocalId)) {
            throw new IllegalArgumentException("Visita no encontrada con ID: " + visitLocalId);
        }
        visitRepository.deleteById(visitLocalId);
    }
}