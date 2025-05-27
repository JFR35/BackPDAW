// src/main/java/com/myobservation/empi/repository/VisitMasterRepository.java
package com.myobservation.empi.repository;

import com.myobservation.empi.model.entity.PatientMasterIndex;
import com.myobservation.empi.model.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitMasterRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatient(PatientMasterIndex patient);
    Optional<Visit> findByVisitUuid(String visitUuid); // <-- ¡CAMBIO AQUÍ!
}