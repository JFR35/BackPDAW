package com.myobservation.empi.repository;

import com.myobservation.empi.model.entity.PatientMasterIndex;
import com.myobservation.empi.model.entity.PractitionerMasterIndex; // Asegúrate de importar esto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientMasterRepository extends JpaRepository<PatientMasterIndex, Long> {

    Optional<PatientMasterIndex> findByNationalId(String nationalId);

    // Buscar por medico asignado, aún no esta implementada la relación en el servicio
    List<PatientMasterIndex> findByAssignedPractitioner(PractitionerMasterIndex practitioner);

    // Buscar por fhirId directamente
    // List<PatientMasterIndex> findByAssignedPractitionerFhirId(String fhirId);
    Optional<PatientMasterIndex> findByEhrId(String ehrId);
}