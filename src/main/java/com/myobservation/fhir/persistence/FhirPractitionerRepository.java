package com.myobservation.fhir.persistence;

import com.myobservation.fhir.fhir.FhirPractitionerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FhirPractitionerRepository extends JpaRepository<FhirPractitionerEntity, Long> {
    Optional<FhirPractitionerEntity> findByUser_UserId(Long userId);

}