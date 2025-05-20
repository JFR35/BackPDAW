package com.myobservation.ehr.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import java.lang.Double;
import java.lang.String;
import java.time.temporal.TemporalAccessor;
import javax.annotation.processing.Generated;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Entity;
import org.ehrbase.openehr.sdk.generator.commons.annotations.OptionFor;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Path;
import org.ehrbase.openehr.sdk.generator.commons.interfaces.PointEventEntity;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.NullFlavour;

@Entity
@Generated(
    value = "org.ehrbase.openehr.sdk.generator.ClassGenerator",
    date = "2025-05-18T10:02:55.905894300+02:00",
    comments = "https://github.com/ehrbase/openEHR_SDK Version: 2.23.0-SNAPSHOT"
)
@OptionFor("POINT_EVENT")
public class BloodPressureAnyEventPointEvent implements PointEventEntity, BloodPressureAnyEventChoice {
  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Systolic
   * Description: Peak systemic arterial blood pressure  - measured in systolic or contraction phase of the heart cycle.
   */
  @Path("/data[at0003]/items[at0004]/value|magnitude")
  private Double systolicMagnitude;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Systolic
   * Description: Peak systemic arterial blood pressure  - measured in systolic or contraction phase of the heart cycle.
   */
  @Path("/data[at0003]/items[at0004]/value|units")
  private String systolicUnits;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/blood pressure/Systolic/null_flavour
   */
  @Path("/data[at0003]/items[at0004]/null_flavour|defining_code")
  private NullFlavour systolicNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Diastolic
   * Description: Minimum systemic arterial blood pressure - measured in the diastolic or relaxation phase of the heart cycle.
   */
  @Path("/data[at0003]/items[at0005]/value|magnitude")
  private Double diastolicMagnitude;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Diastolic
   * Description: Minimum systemic arterial blood pressure - measured in the diastolic or relaxation phase of the heart cycle.
   */
  @Path("/data[at0003]/items[at0005]/value|units")
  private String diastolicUnits;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/blood pressure/Diastolic/null_flavour
   */
  @Path("/data[at0003]/items[at0005]/null_flavour|defining_code")
  private NullFlavour diastolicNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Pulse pressure
   * Description: The difference between the systolic and diastolic pressure.
   */
  @Path("/data[at0003]/items[at1007]/value|magnitude")
  private Double pulsePressureMagnitude;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Pulse pressure
   * Description: The difference between the systolic and diastolic pressure.
   */
  @Path("/data[at0003]/items[at1007]/value|units")
  private String pulsePressureUnits;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/blood pressure/Pulse pressure/null_flavour
   */
  @Path("/data[at0003]/items[at1007]/null_flavour|defining_code")
  private NullFlavour pulsePressureNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Position
   * Description: The position of the individual at the time of measurement.
   */
  @Path("/state[at0007]/items[at0008]/value|defining_code")
  private PositionDefiningCode positionDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/state structure/Position/null_flavour
   */
  @Path("/state[at0007]/items[at0008]/null_flavour|defining_code")
  private NullFlavour positionNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Confounding factors
   * Description: Comment on and record other incidental factors that may be contributing to the blood pressure measurement.  For example, level of anxiety or 'white coat syndrome'; pain or fever; changes in atmospheric pressure etc.
   */
  @Path("/state[at0007]/items[at1052]/value|value")
  private String confoundingFactorsValue;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/state structure/Confounding factors/null_flavour
   */
  @Path("/state[at0007]/items[at1052]/null_flavour|defining_code")
  private NullFlavour confoundingFactorsNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Exertion
   * Description: Details about physical activity undertaken at the time of blood pressure measurement.
   */
  @Path("/state[at0007]/items[at1030]")
  private Cluster exertion;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Sleep status
   * Description: Sleep status - supports interpretation of 24 hour ambulatory blood pressure records.
   */
  @Path("/state[at0007]/items[at1043]/value|defining_code")
  private SleepStatusDefiningCode sleepStatusDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/state structure/Sleep status/null_flavour
   */
  @Path("/state[at0007]/items[at1043]/null_flavour|defining_code")
  private NullFlavour sleepStatusNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Tilt
   * Description: The craniocaudal tilt of the surface on which the person is lying at the time of measurement.
   */
  @Path("/state[at0007]/items[at1005]/value|magnitude")
  private Double tiltMagnitude;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/Tilt
   * Description: The craniocaudal tilt of the surface on which the person is lying at the time of measurement.
   */
  @Path("/state[at0007]/items[at1005]/value|units")
  private String tiltUnits;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/state structure/Tilt/null_flavour
   */
  @Path("/state[at0007]/items[at1005]/null_flavour|defining_code")
  private NullFlavour tiltNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/feeder_audit
   */
  @Path("/feeder_audit")
  private FeederAudit feederAudit;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event/time
   */
  @Path("/time|value")
  private TemporalAccessor timeValue;

  public void setSystolicMagnitude(Double systolicMagnitude) {
     this.systolicMagnitude = systolicMagnitude;
  }

  public Double getSystolicMagnitude() {
     return this.systolicMagnitude ;
  }

  public void setSystolicUnits(String systolicUnits) {
     this.systolicUnits = systolicUnits;
  }

  public String getSystolicUnits() {
     return this.systolicUnits ;
  }

  public void setSystolicNullFlavourDefiningCode(NullFlavour systolicNullFlavourDefiningCode) {
     this.systolicNullFlavourDefiningCode = systolicNullFlavourDefiningCode;
  }

  public NullFlavour getSystolicNullFlavourDefiningCode() {
     return this.systolicNullFlavourDefiningCode ;
  }

  public void setDiastolicMagnitude(Double diastolicMagnitude) {
     this.diastolicMagnitude = diastolicMagnitude;
  }

  public Double getDiastolicMagnitude() {
     return this.diastolicMagnitude ;
  }

  public void setDiastolicUnits(String diastolicUnits) {
     this.diastolicUnits = diastolicUnits;
  }

  public String getDiastolicUnits() {
     return this.diastolicUnits ;
  }

  public void setDiastolicNullFlavourDefiningCode(NullFlavour diastolicNullFlavourDefiningCode) {
     this.diastolicNullFlavourDefiningCode = diastolicNullFlavourDefiningCode;
  }

  public NullFlavour getDiastolicNullFlavourDefiningCode() {
     return this.diastolicNullFlavourDefiningCode ;
  }

  public void setPulsePressureMagnitude(Double pulsePressureMagnitude) {
     this.pulsePressureMagnitude = pulsePressureMagnitude;
  }

  public Double getPulsePressureMagnitude() {
     return this.pulsePressureMagnitude ;
  }

  public void setPulsePressureUnits(String pulsePressureUnits) {
     this.pulsePressureUnits = pulsePressureUnits;
  }

  public String getPulsePressureUnits() {
     return this.pulsePressureUnits ;
  }

  public void setPulsePressureNullFlavourDefiningCode(
      NullFlavour pulsePressureNullFlavourDefiningCode) {
     this.pulsePressureNullFlavourDefiningCode = pulsePressureNullFlavourDefiningCode;
  }

  public NullFlavour getPulsePressureNullFlavourDefiningCode() {
     return this.pulsePressureNullFlavourDefiningCode ;
  }

  public void setPositionDefiningCode(PositionDefiningCode positionDefiningCode) {
     this.positionDefiningCode = positionDefiningCode;
  }

  public PositionDefiningCode getPositionDefiningCode() {
     return this.positionDefiningCode ;
  }

  public void setPositionNullFlavourDefiningCode(NullFlavour positionNullFlavourDefiningCode) {
     this.positionNullFlavourDefiningCode = positionNullFlavourDefiningCode;
  }

  public NullFlavour getPositionNullFlavourDefiningCode() {
     return this.positionNullFlavourDefiningCode ;
  }

  public void setConfoundingFactorsValue(String confoundingFactorsValue) {
     this.confoundingFactorsValue = confoundingFactorsValue;
  }

  public String getConfoundingFactorsValue() {
     return this.confoundingFactorsValue ;
  }

  public void setConfoundingFactorsNullFlavourDefiningCode(
      NullFlavour confoundingFactorsNullFlavourDefiningCode) {
     this.confoundingFactorsNullFlavourDefiningCode = confoundingFactorsNullFlavourDefiningCode;
  }

  public NullFlavour getConfoundingFactorsNullFlavourDefiningCode() {
     return this.confoundingFactorsNullFlavourDefiningCode ;
  }

  public void setExertion(Cluster exertion) {
     this.exertion = exertion;
  }

  public Cluster getExertion() {
     return this.exertion ;
  }

  public void setSleepStatusDefiningCode(SleepStatusDefiningCode sleepStatusDefiningCode) {
     this.sleepStatusDefiningCode = sleepStatusDefiningCode;
  }

  public SleepStatusDefiningCode getSleepStatusDefiningCode() {
     return this.sleepStatusDefiningCode ;
  }

  public void setSleepStatusNullFlavourDefiningCode(
      NullFlavour sleepStatusNullFlavourDefiningCode) {
     this.sleepStatusNullFlavourDefiningCode = sleepStatusNullFlavourDefiningCode;
  }

  public NullFlavour getSleepStatusNullFlavourDefiningCode() {
     return this.sleepStatusNullFlavourDefiningCode ;
  }

  public void setTiltMagnitude(Double tiltMagnitude) {
     this.tiltMagnitude = tiltMagnitude;
  }

  public Double getTiltMagnitude() {
     return this.tiltMagnitude ;
  }

  public void setTiltUnits(String tiltUnits) {
     this.tiltUnits = tiltUnits;
  }

  public String getTiltUnits() {
     return this.tiltUnits ;
  }

  public void setTiltNullFlavourDefiningCode(NullFlavour tiltNullFlavourDefiningCode) {
     this.tiltNullFlavourDefiningCode = tiltNullFlavourDefiningCode;
  }

  public NullFlavour getTiltNullFlavourDefiningCode() {
     return this.tiltNullFlavourDefiningCode ;
  }

  public void setFeederAudit(FeederAudit feederAudit) {
     this.feederAudit = feederAudit;
  }

  public FeederAudit getFeederAudit() {
     return this.feederAudit ;
  }

  public void setTimeValue(TemporalAccessor timeValue) {
     this.timeValue = timeValue;
  }

  public TemporalAccessor getTimeValue() {
     return this.timeValue ;
  }
}
