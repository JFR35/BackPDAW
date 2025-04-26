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
     * Quewy para búsqueda por identificador o nombre, solución simple para buscar dentro del JSON
     * Para una solución más robusta se necesita desnormalizar identifier.value en una columna
     */
    @Query("SELECT p FROM FhirPractitionerEntity p WHERE p.resourcePractitionerJson LIKE %:identifier%")
    List<FhirPractitionerEntity> findByIdentifierContaining(String identifier);
    // Valorar añadir métodos de búsqueda comunes por campos FHIR como findByIdenttifier o incluso


}