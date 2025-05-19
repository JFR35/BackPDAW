package com.myobservation.ehr.service;

import com.myobservation.ehr.model.definition.PresionSanguineaComposition;
import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BloodPressureService {
    private static final Logger logger = LoggerFactory.getLogger(BloodPressureService.class);
    private final OpenEhrClient openEhrClient;

    public BloodPressureService(OpenEhrClient openEhrClient) {
        this.openEhrClient = openEhrClient;
    }

    public String createComposition(UUID ehrId, PresionSanguineaComposition composition) {
        try {
            logger.info("Creating composition for EHR ID: {}", ehrId);
            UUID compositionId = openEhrClient.compositionEndpoint(ehrId)
                    .mergeCompositionEntity(PresionSanguineaComposition.class,composition);
            logger.info("Composition created with ID: {}", compositionId);
            return compositionId.toString();
        } catch (Exception e) {
            logger.error("Error creating composition for EHR ID: {}", ehrId, e);
            throw new RuntimeException("Error creating composition: " + e.getMessage(), e);
        }
    }
    public UUID createEhr() {
        try {
            logger.info("Creating new EHR");
            UUID ehrId = openEhrClient.ehrEndpoint().createEhr();
            logger.info("EHR created with ID: {}", ehrId);
            return ehrId;
        } catch (Exception e) {
            logger.error("Error creating EHR", e);
            throw new RuntimeException("Error creating EHR: " + e.getMessage(), e);
        }
    }

}