
package com.myobservation.empi.repository;

import com.myobservation.empi.model.entity.PractitionerMasterIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PractitionerRepository extends JpaRepository<PractitionerMasterIndex, Long> {
    Optional<PractitionerMasterIndex> findByFhirId(String fhirId);
    Optional<PractitionerMasterIndex> findByNationalId(String nationalId);
}