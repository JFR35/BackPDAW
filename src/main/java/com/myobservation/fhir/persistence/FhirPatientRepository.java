package com.myobservation.fhir.persistence;

import com.myobservation.fhir.profile.FhirPatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FhirPatientRepository extends JpaRepository<FhirPatientEntity, Long> {
    Optional<FhirPatientEntity> findById(Long id);

}