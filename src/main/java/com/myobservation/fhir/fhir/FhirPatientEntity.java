package com.myobservation.fhir.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "patients")
public class FhirPatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_patient_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String resourcePatientJson;

    public String getResourcePatientJson() {
        return resourcePatientJson;
    }

    public void setResourcePatientJson(String resourcePatientJson) {
        this.resourcePatientJson = resourcePatientJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}