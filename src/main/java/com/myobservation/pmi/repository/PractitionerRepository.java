
package com.myobservation.pmi.repository;

import com.myobservation.pmi.entity.PractitionerMasterIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PractitionerRepository extends JpaRepository<PractitionerMasterIndex, Long> {
    Optional<PractitionerMasterIndex> findByFhirId(String fhirId);
    Optional<PractitionerMasterIndex> findByNationalId(String nationalId);
}