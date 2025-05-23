package com.myobservation.ehrbridge.pojos.definition;

import java.lang.String;
import javax.annotation.processing.Generated;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Entity;
import org.ehrbase.openehr.sdk.generator.commons.annotations.OptionFor;
import org.ehrbase.openehr.sdk.generator.commons.annotations.Path;
import org.ehrbase.openehr.sdk.generator.commons.interfaces.RMEntity;

@Entity
@Generated(
    value = "org.ehrbase.openehr.sdk.generator.ClassGenerator",
    date = "2025-05-22T10:35:38.140521+02:00",
    comments = "https://github.com/ehrbase/openEHR_SDK Version: 2.23.0-SNAPSHOT"
)
@OptionFor("DV_TEXT")
public class BloodPressureLocationOfMeasurementDvText implements RMEntity, BloodPressureLocationOfMeasurementChoice {
  /**
   * Path: blood_pressure/Blood pressure/Location of measurement/Location of measurement
   * Description: Simple body site where blood pressure was measured.
   */
  @Path("|value")
  private String locationOfMeasurementValue;

  public void setLocationOfMeasurementValue(String locationOfMeasurementValue) {
     this.locationOfMeasurementValue = locationOfMeasurementValue;
  }

  public String getLocationOfMeasurementValue() {
     return this.locationOfMeasurementValue ;
  }
}
