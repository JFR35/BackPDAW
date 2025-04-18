package com.myobservation.fhir.persistence;

import jakarta.persistence.*;
import org.checkerframework.checker.index.qual.IndexFor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "practitioners")
public class FhirPractitionerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_practitioner_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String resourcePractitionerJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourcePractitionerJson() {
        return resourcePractitionerJson;
    }

    public void setResourcePractitionerJson(String resourcePractitionerJson) {
        this.resourcePractitionerJson = resourcePractitionerJson;
    }
}
