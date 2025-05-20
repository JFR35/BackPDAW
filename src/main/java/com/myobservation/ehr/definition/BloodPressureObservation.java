package com.myobservation.ehr.model.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.PartyProxy;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Archetype;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Choice;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Entity;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Path;
import org.ehrbase.openehr.sdk.generator.commons.interfaces.EntryEntity;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.Language;
import org.ehrbase.openehr.sdk.generator.commons.shareddefinition.NullFlavour;

@Entity
@Archetype("openEHR-EHR-OBSERVATION.blood_pressure.v2")
@Generated(
    value = "org.ehrbase.openehr.sdk.generator.ClassGenerator",
    date = "2025-05-18T10:02:55.882871+02:00",
    comments = "https://github.com/ehrbase/openEHR_SDK Version: 2.23.0-SNAPSHOT"
)
public class BloodPressureObservation implements EntryEntity {
  /**
   * Path: Presión Sanguínea/Blood pressure/origin
   */
  @Path("/data[at0001]/origin|value")
  private TemporalAccessor originValue;

  /**
   * Path: Presión Sanguínea/Blood pressure/Tree/Location of measurement/null_flavour
   */
  @Path("/protocol[at0011]/items[at0014]/null_flavour|defining_code")
  private NullFlavour locationOfMeasurementNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Structured measurement location
   * Description: Structured anatomical location of where the measurement was taken.
   */
  @Path("/protocol[at0011]/items[at1057]")
  private List<Cluster> structuredMeasurementLocation;

  /**
   * Path: Presión Sanguínea/Blood pressure/Method
   * Description: Method of measurement of blood pressure.
   */
  @Path("/protocol[at0011]/items[at1035]/value|defining_code")
  private MethodDefiningCode methodDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Tree/Method/null_flavour
   */
  @Path("/protocol[at0011]/items[at1035]/null_flavour|defining_code")
  private NullFlavour methodNullFlavourDefiningCode;

  /**
   * Path: Presión Sanguínea/Blood pressure/Device
   * Description: Details about sphygmomanometer or other device used to measure the blood pressure.
   */
  @Path("/protocol[at0011]/items[at1025]")
  private Cluster device;

  /**
   * Path: Presión Sanguínea/Blood pressure/Extension
   * Description: Additional information required to capture local context or to align with other reference models/formalisms.
   * Comment: For example: Local hospital departmental infomation or additional metadata to align with FHIR or CIMI equivalents.
   */
  @Path("/protocol[at0011]/items[at1058]")
  private List<Cluster> extension;

  /**
   * Path: Presión Sanguínea/Blood pressure/subject
   */
  @Path("/subject")
  private PartyProxy subject;

  /**
   * Path: Presión Sanguínea/Blood pressure/language
   */
  @Path("/language")
  private Language language;

  /**
   * Path: Presión Sanguínea/Blood pressure/feeder_audit
   */
  @Path("/feeder_audit")
  private FeederAudit feederAudit;

  /**
   * Path: Presión Sanguínea/Blood pressure/Any event
   * Description: Default, unspecified point in time or interval event which may be explicitly defined in a template or at run-time.
   */
  @Path("/data[at0001]/events[at0006]")
  @Choice
  private List<BloodPressureAnyEventChoice> anyEvent;

  /**
   * Path: Presión Sanguínea/Blood pressure/Location of measurement
   * Description: Simple body site where blood pressure was measured.
   */
  @Path("/protocol[at0011]/items[at0014]/value")
  @Choice
  private BloodPressureLocationOfMeasurementChoice locationOfMeasurement;

  public void setOriginValue(TemporalAccessor originValue) {
     this.originValue = originValue;
  }

  public TemporalAccessor getOriginValue() {
     return this.originValue ;
  }

  public void setLocationOfMeasurementNullFlavourDefiningCode(
      NullFlavour locationOfMeasurementNullFlavourDefiningCode) {
     this.locationOfMeasurementNullFlavourDefiningCode = locationOfMeasurementNullFlavourDefiningCode;
  }

  public NullFlavour getLocationOfMeasurementNullFlavourDefiningCode() {
     return this.locationOfMeasurementNullFlavourDefiningCode ;
  }

  public void setStructuredMeasurementLocation(List<Cluster> structuredMeasurementLocation) {
     this.structuredMeasurementLocation = structuredMeasurementLocation;
  }

  public List<Cluster> getStructuredMeasurementLocation() {
     return this.structuredMeasurementLocation ;
  }

  public void setMethodDefiningCode(MethodDefiningCode methodDefiningCode) {
     this.methodDefiningCode = methodDefiningCode;
  }

  public MethodDefiningCode getMethodDefiningCode() {
     return this.methodDefiningCode ;
  }

  public void setMethodNullFlavourDefiningCode(NullFlavour methodNullFlavourDefiningCode) {
     this.methodNullFlavourDefiningCode = methodNullFlavourDefiningCode;
  }

  public NullFlavour getMethodNullFlavourDefiningCode() {
     return this.methodNullFlavourDefiningCode ;
  }

  public void setDevice(Cluster device) {
     this.device = device;
  }

  public Cluster getDevice() {
     return this.device ;
  }

  public void setExtension(List<Cluster> extension) {
     this.extension = extension;
  }

  public List<Cluster> getExtension() {
     return this.extension ;
  }

  public void setSubject(PartyProxy subject) {
     this.subject = subject;
  }

  public PartyProxy getSubject() {
     return this.subject ;
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

  public void setAnyEvent(List<BloodPressureAnyEventChoice> anyEvent) {
     this.anyEvent = anyEvent;
  }

  public List<BloodPressureAnyEventChoice> getAnyEvent() {
     return this.anyEvent ;
  }

  public void setLocationOfMeasurement(
      BloodPressureLocationOfMeasurementChoice locationOfMeasurement) {
     this.locationOfMeasurement = locationOfMeasurement;
  }

  public BloodPressureLocationOfMeasurementChoice getLocationOfMeasurement() {
     return this.locationOfMeasurement ;
  }
}
