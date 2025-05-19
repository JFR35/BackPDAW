package com.myobservation.ehr.mapper;

import com.myobservation.ehr.model.definition.BloodPressureAnyEventIntervalEvent;
import com.myobservation.ehr.model.definition.BloodPressureObservation;
import com.myobservation.ehr.model.definition.PositionDefiningCode;
import com.myobservation.ehr.model.definition.PresionSanguineaComposition;
import com.myobservation.ehr.model.dto.BloodPressureDto;

import com.nedap.archie.rm.generic.PartyIdentified;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Category;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Language;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Setting;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Territory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.List;

public class BloodPressureMapper {
    private static final Logger logger = LoggerFactory.getLogger(BloodPressureMapper.class);

    public static PresionSanguineaComposition toComposition(BloodPressureDto dto) {
        PresionSanguineaComposition composition = new PresionSanguineaComposition();

        // Campos obligatorios de PresionSanguineaComposition
        composition.setCategoryDefiningCode(Category.EVENT);
        composition.setLanguage(Language.ES);
        composition.setTerritory(Territory.ES);
        composition.setSettingDefiningCode(Setting.SECONDARY_MEDICAL_CARE);
        composition.setStartTimeValue(dto.getMeasurementTime().atZone(ZoneId.systemDefault()));

        // Establecer composer (obligatorio para OpenEHR)
        PartyIdentified composer = new PartyIdentified();
        composer.setName("Sistema automático");
        composition.setComposer(composer);

        // Establecer el estado y el ID del informe (opcional, pero buena práctica)
        composition.setStatusValue("final");
        composition.setReportIdValue("BP-" + System.currentTimeMillis());

        // Crear observación
        BloodPressureObservation observation = new BloodPressureObservation();
        observation.setLanguage(Language.ES);
        observation.setOriginValue(dto.getMeasurementTime().atZone(ZoneId.systemDefault()));

        // Crear evento
        BloodPressureAnyEventIntervalEvent event = new BloodPressureAnyEventIntervalEvent();
        event.setSystolicMagnitude(dto.getSystolic());
        event.setSystolicUnits(dto.getUnit());
        event.setDiastolicMagnitude(dto.getDiastolic());
        event.setDiastolicUnits(dto.getUnit());
        event.setTimeValue(dto.getMeasurementTime().atZone(ZoneId.systemDefault()));

        // Configurar posición si está presente
        if (dto.getPosition() != null) {
            try {
                event.setPositionDefiningCode(PositionDefiningCode.valueOf(dto.getPosition()));
            } catch (IllegalArgumentException e) {
                logger.error("Invalid position code: {}", dto.getPosition(), e);
            }
        }

        // Asignar evento a la observación
        observation.setAnyEvent(List.of(event));

        // Asignar observación a la composición
        composition.setBloodPressure(observation);

        return composition;
    }
}