package com.myobservation.empi.service;

import com.myobservation.ehrbridge.model.BloodPressureRequestDTO;
import com.myobservation.ehrbridge.service.EhrBaseService;
import com.myobservation.empi.model.dto.BloodPressureMeasurementDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ObservationService {

    private final EhrBaseService ehrBaseService;

    public ObservationService(EhrBaseService ehrBaseService) {
        this.ehrBaseService = ehrBaseService;
    }

    public void recordBloodPressure(BloodPressureMeasurementDto dto, String patientId) {
        // Mapear el DTO que llega desde EMPI al DTO requerido por ehrbridge
        BloodPressureRequestDTO requestDTO = new BloodPressureRequestDTO();
        requestDTO.setPatientId(patientId);
        requestDTO.setSystolic(dto.getSystolicMagnitude());
        requestDTO.setDiastolic(dto.getDiastolicMagnitude());
        requestDTO.setLocation(dto.getLocation());
        requestDTO.setComposerName(dto.getMeasuredBy());  // Si tienes este dato

        // Obtener o crear EHR para este paciente
        UUID ehrId = ehrBaseService.createPatientEhr(patientId); // Aquí puedes aplicar lógica de "si existe, no lo crees"

        // Guardar la composición
        ehrBaseService.createBloodPressureComposition(requestDTO, ehrId.toString());
    }
}
