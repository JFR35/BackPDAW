package com.myobservation.pmi.repository;

import com.myobservation.pmi.entity.PatientMasterIndex;
import com.myobservation.pmi.entity.PractitionerMasterIndex; // Asegúrate de importar esto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientMasterRepository extends JpaRepository<PatientMasterIndex, Long> {

    Optional<PatientMasterIndex> findByNationalId(String nationalId);

    // --- ¡Añade este método! ---
    List<PatientMasterIndex> findByAssignedPractitioner(PractitionerMasterIndex practitioner);

    // Si también necesitaras buscar por el fhirId del practicante asignado directamente:
    // List<PatientMasterIndex> findByAssignedPractitionerFhirId(String fhirId);

    // Otros métodos de búsqueda si los tienes...
    // Optional<PatientMasterIndex> findByFhirId(String fhirId);
    // Optional<PatientMasterIndex> findByEhrId(UUID ehrId);
}