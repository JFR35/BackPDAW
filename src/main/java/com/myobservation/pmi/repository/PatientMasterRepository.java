package com.myobservation.pmi.repository;

import com.myobservation.pmi.model.entity.PatientMasterIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientMasterRepository extends JpaRepository<PatientMasterIndex, Long> {
    Optional<PatientMasterIndex> findByInternalId(String internalId);
    Optional<PatientMasterIndex> findByNationalId(String nationalId);
}
