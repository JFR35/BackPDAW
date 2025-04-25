package com.myobservation.fhir.persistence;

import com.myobservation.fhir.fhir.FhirPractitionerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FhirPractitionerRepository extends JpaRepository<FhirPractitionerEntity, Long> {
    /**
     * Busca un paciente por su identificador.
     * @param userId ID del paciente a buscar.
     * @return Un {@link Optional} con el paciente si lo encuentra o vacío si no existe.
     */
    Optional<FhirPractitionerEntity> findByUser_UserId(Long userId);

    // Valorar añadir métodos de búsqueda comunes por campos FHIR como findByIdenttifier o incluso
    // implementar alguna operacion de búsqueda FHIR como _search.

}