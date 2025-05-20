package com.myobservation.ehr.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.rm.generic.PartyProxy;
import com.nedap.archie.rm.support.identification.ObjectVersionId;
import java.lang.String;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Archetype;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Entity;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Id;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Path;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Template;
import org.ehrbase.openehr.sdk.generator.commons.interfaces.CompositionEntity;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Category;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Language;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.NullFlavour;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Setting;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Territory;

@Entity
@Archetype("openEHR-EHR-COMPOSITION.report.v1")
@Generated(
    value = "org.ehrbase.openehr.sdk.generator.ClassGenerator",
    date = "2025-05-18T10:02:55.844865600+02:00",
    comments = "https://github.com/ehrbase/openEHR_SDK Version: 2.23.0-SNAPSHOT"
)
@Template("Presión Sanguínea")
public class PresionSanguineaComposition implements CompositionEntity {
  /**
   * Path: Presión Sanguínea/category
   */
  @Path("/category|defining_code")
  private Category categoryDefiningCode;

  /**
   * Path: Presión Sanguínea/context/Report ID
   * Description: Identification information about the report.
   */
  @Path("/context/other_context[at0001]/items[at0002]/value|value")
  private String reportIdValue;

  /**
   * Path: Presión Sanguínea/context/Tree/Report ID/null_flavour
   */
  @Path("/context/other_context[at0001]/items[at0002]/null_flavour|defining_code")
  private NullFlavour reportIdNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/context/Status
   * Description: The status of the entire report. Note: This is not the status of any of the report components.
   */
  @Path("/context/other_context[at0001]/items[at0005]/value|value")
  private String statusValue;

  /**
   * Path: Presión Sanguínea/context/Tree/Status/null_flavour
   */
  @Path("/context/other_context[at0001]/items[at0005]/null_flavour|defining_code")
  private NullFlavour statusNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/context/Extension
   * Description: Additional information required to capture local context or to align with other reference models/formalisms.
   * Comment: For example: local information requirements or additional metadata to align with FHIR or CIMI equivalents.
   */
  @Path("/context/other_context[at0001]/items[at0006]")
  private List<Cluster> extension;

  /**
   * Path: Presión Sanguínea/context/start_time
   */
  @Path("/context/start_time|value")
  private TemporalAccessor startTimeValue;

  /**
   * Path: Presión Sanguínea/context/participations
   */
  @Path("/context/participations")
  private List<Participation> participations;

  /**
   * Path: Presión Sanguínea/context/end_time
   */
  @Path("/context/end_time|value")
  private TemporalAccessor endTimeValue;

  /**
   * Path: Presión Sanguínea/context/location
   */
  @Path("/context/location")
  private String location;

  /**
   * Path: Presión Sanguínea/context/health_care_facility
   */
  @Path("/context/health_care_facility")
  private PartyIdentified healthCareFacility;

  /**
   * Path: Presión Sanguínea/context/setting
   */
  @Path("/context/setting|defining_code")
  private Setting settingDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure
   * Description: The local measurement of arterial blood pressure which is a surrogate for arterial pressure in the systemic circulation.
   * Comment: Most commonly, use of the term 'blood pressure' refers to measurement of brachial artery pressure in the upper arm.
   */
  @Path("/content[openEHR-EHR-OBSERVATION.blood_pressure.v2]")
  private BloodPressureObservation bloodPressure;

  /**
   * Path: Presión Sanguínea/composer
   */
  @Path("/composer")
  private PartyProxy composer;

  /**
   * Path: Presión Sanguínea/language
   */
  @Path("/language")
  private Language language;

  /**
   * Path: Presión Sanguínea/feeder_audit
   */
  @Path("/feeder_audit")
  private FeederAudit feederAudit;

  /**
   * Path: Presión Sanguínea/territory
   */
  @Path("/territory")
  private Territory territory;

  @Id
  private ObjectVersionId versionUid;

  public void setCategoryDefiningCode(Category categoryDefiningCode) {
     this.categoryDefiningCode = categoryDefiningCode;
  }

  public Category getCategoryDefiningCode() {
     return this.categoryDefiningCode ;
  }

  public void setReportIdValue(String reportIdValue) {
     this.reportIdValue = reportIdValue;
  }

  public String getReportIdValue() {
     return this.reportIdValue ;
  }

  public void setReportIdNullFlavourDefiningCode(NullFlavour reportIdNullFlavourDefiningCode) {
     this.reportIdNullFlavourDefiningCode = reportIdNullFlavourDefiningCode;
  }

  public NullFlavour getReportIdNullFlavourDefiningCode() {
     return this.reportIdNullFlavourDefiningCode ;
  }

  public void setStatusValue(String statusValue) {
     this.statusValue = statusValue;
  }

  public String getStatusValue() {
     return this.statusValue ;
  }

  public void setStatusNullFlavourDefiningCode(NullFlavour statusNullFlavourDefiningCode) {
     this.statusNullFlavourDefiningCode = statusNullFlavourDefiningCode;
  }

  public NullFlavour getStatusNullFlavourDefiningCode() {
     return this.statusNullFlavourDefiningCode ;
  }

  public void setExtension(List<Cluster> extension) {
     this.extension = extension;
  }

  public List<Cluster> getExtension() {
     return this.extension ;
  }

  public void setStartTimeValue(TemporalAccessor startTimeValue) {
     this.startTimeValue = startTimeValue;
  }

  public TemporalAccessor getStartTimeValue() {
     return this.startTimeValue ;
  }

  public void setParticipations(List<Participation> participations) {
     this.participations = participations;
  }

  public List<Participation> getParticipations() {
     return this.participations ;
  }

  public void setEndTimeValue(TemporalAccessor endTimeValue) {
     this.endTimeValue = endTimeValue;
  }

  public TemporalAccessor getEndTimeValue() {
     return this.endTimeValue ;
  }

  public void setLocation(String location) {
     this.location = location;
  }

  public String getLocation() {
     return this.location ;
  }

  public void setHealthCareFacility(PartyIdentified healthCareFacility) {
     this.healthCareFacility = healthCareFacility;
  }

  public PartyIdentified getHealthCareFacility() {
     return this.healthCareFacility ;
  }

  public void setSettingDefiningCode(Setting settingDefiningCode) {
     this.settingDefiningCode = settingDefiningCode;
  }

  public Setting getSettingDefiningCode() {
     return this.settingDefiningCode ;
  }

  public void setBloodPressure(BloodPressureObservation bloodPressure) {
     this.bloodPressure = bloodPressure;
  }

  public BloodPressureObservation getBloodPressure() {
     return this.bloodPressure ;
  }

  public void setComposer(PartyProxy composer) {
     this.composer = composer;
  }

  public PartyProxy getComposer() {
     return this.composer ;
  }

  public void setLanguage(Language language) {
     this.language = language;
  }

  public Language getLanguage() {
     return this.language ;
  }

  public void setFeederAudit(FeederAudit feederAudit) {
     this.feederAudit = feederAudit;
  }

  public FeederAudit getFeederAudit() {
     return this.feederAudit ;
  }

  public void setTerritory(Territory territory) {
     this.territory = territory;
  }

  public Territory getTerritory() {
     return this.territory ;
  }

  public ObjectVersionId getVersionUid() {
     return this.versionUid ;
  }

  public void setVersionUid(ObjectVersionId versionUid) {
     this.versionUid = versionUid;
  }
}
