package com.myobservation.fhir.persistence;

import com.myobservation.fhir.fhir.FhirPractitionerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FhirPractitionerRepository extends JpaRepository<FhirPractitionerEntity, Long> {
    /**
     * Busca un paciente por su identificador.
     * @param userId ID del paciente a buscar.
     * @return Un {@link Optional} con el paciente si lo encuentra o vacío si no existe.
     */
    Optional<FhirPractitionerEntity> findByUser_UserId(Long userId);
    /**
     /**
     * Busca Practitioners cuyo identifier.value contenga el texto proporcionado.
     * @param identifier Texto a buscar en el campo identifier.value del JSON.
     * @return Lista de {@link FhirPractitionerEntity} que coinciden con el criterio.
     */
    @Query(value = "SELECT * FROM practitioners WHERE resource_practitioner_json #>> '{identifier,0,value}' LIKE '%' || :identifier || '%'", nativeQuery = true)
    List<FhirPractitionerEntity> findByIdentifierContaining(String identifier);

}