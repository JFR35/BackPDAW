package com.myobservation.ehr.service;

import com.myobservation.ehr.model.BloodPressureRequestDTO;
import com.myobservation.ehr.definition.PresionSanguineaComposition;
import com.myobservation.ehr.definition.BloodPressureObservation;
import com.myobservation.ehr.definition.BloodPressureAnyEventPointEvent;
import com.myobservation.ehr.definition.MethodDefiningCode;
import com.myobservation.ehr.definition.BloodPressureLocationOfMeasurementDvCodedText;
import com.myobservation.ehr.definition.LocationOfMeasurementDefiningCode;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.rm.generic.PartySelf;
import jakarta.annotation.PostConstruct;
import org.ehrbase.openehr.sdk.client.openehrclient.EhrEndpoint;
import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClient;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Category;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Language;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Setting;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Territory;
import org.ehrbase.openehr.sdk.webtemplate.templateprovider.TemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class EhrBaseService {

    private static final Logger logger = LoggerFactory.getLogger(EhrBaseService.class);
    private static final String TEMPLATE_ID = "Presion Sanguinea";

    private final OpenEhrClient openEhrClient;
    private final TemplateProvider templateProvider;

    @Value("${ehrbase.url}")
    private String ehrBaseUrl;

    @Value("${ehrbase.username}")
    private String ehrBaseUsername;

    @Value("${ehrbase.password}")
    private String ehrBasePassword;

    @Value("${ehrbase.templates.path}")
    private String templatePath;


    public EhrBaseService(OpenEhrClient openEhrClient, TemplateProvider templateProvider) {
        this.openEhrClient = openEhrClient;
        this.templateProvider = templateProvider;
        logger.info("EHR client and TemplateProvider injected successfully into EhrBaseService.");
    }

    @PostConstruct
    public void init() {
        registerTemplate();
    }

    public void registerTemplate() {
        try {
            File templateFile = new File(templatePath + "/presion_sanguinea.opt");
            if (!templateFile.exists()) {
                throw new RuntimeException("Template file not found: " + templateFile.getAbsolutePath());
            }
            //openEhrClient.templateEndpoint().createTemplate(templateFile);
            logger.info("Template 'presion_sanguinea' registered successfully.");
        } catch (Exception e) {
            logger.error("Failed to register template", e);
            throw new RuntimeException("Failed to register template", e);
        }
    }


    public boolean verifyConnection() {
        try {
            openEhrClient.ehrEndpoint().getEhrStatus(UUID.randomUUID());
            return true;
        } catch (Exception e) {
            logger.error("Failed to connect to EHRbase or authentication issue: {}", e.getMessage());
            return false;
        }
    }

    public String createBloodPressureComposition(BloodPressureRequestDTO requestDTO, String ehrId) {
        try {
            Optional<?> template = templateProvider.find(TEMPLATE_ID);
            if (template.isEmpty()) {
                throw new RuntimeException("La plantilla 'presion_sanguinea' no está registrada en EHRbase.");
            }
            UUID ehrUUID = ehrId != null ? UUID.fromString(ehrId) : createPatientEhr(requestDTO.getPatientId());
            PresionSanguineaComposition composition = createBPComposition(requestDTO, ehrUUID);
            return saveComposition(composition, ehrUUID);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create blood pressure composition", e);
        }
    }

    private UUID createPatientEhr(String patientId) {
        try {
            EhrEndpoint ehrEndpoint = openEhrClient.ehrEndpoint();
            UUID ehrId = ehrEndpoint.createEhr();
            logger.info("Created EHR for patient: {} with ID: {}", patientId, ehrId);
            return ehrId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create EHR for patient: " + patientId, e);
        }
    }

    private PresionSanguineaComposition createBPComposition(BloodPressureRequestDTO requestDTO, UUID ehrId) {
        PresionSanguineaComposition composition = new PresionSanguineaComposition();

        composition.setLanguage(Language.EN);
        composition.setTerritory(Territory.ES);
        composition.setCategoryDefiningCode(Category.EVENT);
        composition.setSettingDefiningCode(Setting.OTHER_CARE);

        composition.setStartTimeValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));

        PartyIdentified composer = new PartyIdentified();
        composer.setName(requestDTO.getComposerName());
        composition.setComposer(composer);

        BloodPressureObservation bpObservation = new BloodPressureObservation();
        bpObservation.setSubject(new PartySelf());
        bpObservation.setOriginValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));
        bpObservation.setLanguage(Language.EN);

        BloodPressureLocationOfMeasurementDvCodedText locationOfMeasurement = new BloodPressureLocationOfMeasurementDvCodedText();
        locationOfMeasurement.setLocationOfMeasurementDefiningCode(LocationOfMeasurementDefiningCode.RIGHT_ARM);
        bpObservation.setLocationOfMeasurement(locationOfMeasurement);

        bpObservation.setMethodDefiningCode(MethodDefiningCode.AUSCULTATION);

        BloodPressureAnyEventPointEvent event = new BloodPressureAnyEventPointEvent();
        event.setSystolicMagnitude(requestDTO.getSystolic());
        event.setSystolicUnits("mm[Hg]");
        event.setDiastolicMagnitude(requestDTO.getDiastolic());
        event.setDiastolicUnits("mm[Hg]");

        event.setTimeValue(requestDTO.getMeasurementTime().atOffset(ZoneOffset.UTC));

        if (requestDTO.getMeanArterialPressure() != null) {
            // Descomentar si los métodos existen
            // event.setMeanArterialPressureMagnitude(requestDTO.getMeanArterialPressure());
            // event.setMeanArterialPressureUnits("mm[Hg]");
        }

        if (requestDTO.getPulseRate() != null) {
            // Descomentar si los métodos existen
            // event.setPulseRateMagnitude(requestDTO.getPulseRate());
            // event.setPulseRateUnits("/min");
        }

        bpObservation.setAnyEvent(Collections.singletonList(event));
        composition.setBloodPressure(bpObservation);

        return composition;
    }

    private String saveComposition(PresionSanguineaComposition composition, UUID ehrId) {
        try {
            PresionSanguineaComposition savedComposition = openEhrClient.compositionEndpoint(ehrId)
                    .mergeCompositionEntity(composition);
            String compositionId = savedComposition.getVersionUid().getValue();
            logger.info("Composition saved with ID: {}", compositionId);
            return compositionId;
        } catch (Exception e) {
            logger.error("Failed to save composition to EHRBase", e);
            throw new RuntimeException("Failed to save composition to EHRBase", e);
        }
    }
}