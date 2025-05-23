package com.myobservation.pmi.mapper;

import com.myobservation.pmi.model.dto.PMIRequest;
import com.myobservation.pmi.model.dto.PMIResponse;
import com.myobservation.pmi.model.entity.PatientMasterIndex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapeador para convertir entre la entidad {@link PatientMasterIndex} y los DTOs {@link PMIRequest} y {@link PMIResponse}.
 */
@Mapper(componentModel = "spring")
public interface PatientMapper {

    /**
     * Convierte un objeto {@link PMIRequest} en una entidad {@link PatientMasterIndex}.
     * @param request DTO de solicitud con los datos del paciente.
     * @return Entidad {@link PatientMasterIndex}.
     */
    PatientMasterIndex toEntity(PMIRequest request);

    /**
     * Convierte una entidad {@link PatientMasterIndex} en un objeto {@link PMIResponse}.
     * @param entity Entidad de paciente a transformar.
     * @return Objeto {@link PMIResponse} con los datos estructurados.
     */
    @Mapping(source = "patientMasterId", target = "patientMasterId")
    @Mapping(source = "createdAt", target = "createdAt")
    PMIResponse toResponse(PatientMasterIndex entity);

    /**
     * Actualiza la entidad {@link PatientMasterIndex} con los datos del objeto {@link PMIRequest}.
     * @param request DTO con los datos que se actualizarán.
     * @param entity Entidad de paciente a actualizar.
     */
    void updateEntityFromRequest(PMIRequest request, @MappingTarget PatientMasterIndex entity);
}
