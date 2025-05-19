package com.myobservation.ehr.model.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import java.lang.Double;
import java.lang.String;
import java.time.temporal.TemporalAccessor;
import javax.annotation.processing.Generated;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.NullFlavour;

@Generated(
        value = "org.ehrbase.openehr.sdk.generator.ClassGenerator",
        date = "2025-05-19T12:33:17.616356700+02:00",
        comments = "https://github.com/ehrbase/openEHR_SDK Version: 2.23.0-SNAPSHOT"
)
public interface BloodPressureAnyEventChoice {
    NullFlavour getPulsePressureNullFlavourDefiningCode();

    void setPulsePressureNullFlavourDefiningCode(NullFlavour pulsePressureNullFlavourDefiningCode);

    NullFlavour getConfoundingFactorsNullFlavourDefiningCode();

    void setConfoundingFactorsNullFlavourDefiningCode(
            NullFlavour confoundingFactorsNullFlavourDefiningCode);

    Double getPulsePressureMagnitude();

    void setPulsePressureMagnitude(Double pulsePressureMagnitude);

    TemporalAccessor getTimeValue();

    void setTimeValue(TemporalAccessor timeValue);

    NullFlavour getPositionNullFlavourDefiningCode();

    void setPositionNullFlavourDefiningCode(NullFlavour positionNullFlavourDefiningCode);

    String getConfoundingFactorsValue();

    void setConfoundingFactorsValue(String confoundingFactorsValue);

    PositionDefiningCode getPositionDefiningCode();

    void setPositionDefiningCode(PositionDefiningCode positionDefiningCode);

    NullFlavour getSleepStatusNullFlavourDefiningCode();

    void setSleepStatusNullFlavourDefiningCode(NullFlavour sleepStatusNullFlavourDefiningCode);

    String getTiltUnits();

    void setTiltUnits(String tiltUnits);

    FeederAudit getFeederAudit();

    void setFeederAudit(FeederAudit feederAudit);

    String getDiastolicUnits();

    void setDiastolicUnits(String diastolicUnits);

    NullFlavour getDiastolicNullFlavourDefiningCode();

    void setDiastolicNullFlavourDefiningCode(NullFlavour diastolicNullFlavourDefiningCode);

    Cluster getExertion();

    void setExertion(Cluster exertion);

    String getSystolicUnits();

    void setSystolicUnits(String systolicUnits);

    SleepStatusDefiningCode getSleepStatusDefiningCode();

    void setSleepStatusDefiningCode(SleepStatusDefiningCode sleepStatusDefiningCode);

    NullFlavour getSystolicNullFlavourDefiningCode();

    void setSystolicNullFlavourDefiningCode(NullFlavour systolicNullFlavourDefiningCode);

    String getPulsePressureUnits();

    void setPulsePressureUnits(String pulsePressureUnits);

    Double getDiastolicMagnitude();

    void setDiastolicMagnitude(Double diastolicMagnitude);

    NullFlavour getTiltNullFlavourDefiningCode();

    void setTiltNullFlavourDefiningCode(NullFlavour tiltNullFlavourDefiningCode);

    Double getSystolicMagnitude();

    void setSystolicMagnitude(Double systolicMagnitude);

    Double getTiltMagnitude();

    void setTiltMagnitude(Double tiltMagnitude);
}
