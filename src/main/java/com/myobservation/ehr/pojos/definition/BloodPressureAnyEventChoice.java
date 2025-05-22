package com.myobservation.ehr.pojos.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import java.lang.Double;
import java.lang.String;
import java.time.temporal.TemporalAccessor;
import javax.annotation.processing.Generated;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.NullFlavour;

@Generated(
    value = "org.ehrbase.openehr.sdk.generator.ClassGenerator",
    date = "2025-05-22T10:35:38.120964+02:00",
    comments = "https://github.com/ehrbase/openEHR_SDK Version: 2.23.0-SNAPSHOT"
)
public interface BloodPressureAnyEventChoice {
  Cluster getExertion();

  void setExertion(Cluster exertion);

  TemporalAccessor getTimeValue();

  void setTimeValue(TemporalAccessor timeValue);

  String getSystolicUnits();

  void setSystolicUnits(String systolicUnits);

  NullFlavour getSystolicNullFlavourDefiningCode();

  void setSystolicNullFlavourDefiningCode(NullFlavour systolicNullFlavourDefiningCode);

  String getDiastolicUnits();

  void setDiastolicUnits(String diastolicUnits);

  NullFlavour getDiastolicNullFlavourDefiningCode();

  void setDiastolicNullFlavourDefiningCode(NullFlavour diastolicNullFlavourDefiningCode);

  Double getSystolicMagnitude();

  void setSystolicMagnitude(Double systolicMagnitude);

  Double getDiastolicMagnitude();

  void setDiastolicMagnitude(Double diastolicMagnitude);

  FeederAudit getFeederAudit();

  void setFeederAudit(FeederAudit feederAudit);
}
