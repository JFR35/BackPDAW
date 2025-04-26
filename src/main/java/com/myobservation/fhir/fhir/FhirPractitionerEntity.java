package com.myobservation.fhir.fhir;

import com.myobservation.auth.entity.MyUser;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Representa la entidad de un Practitioner en FHIR alamcenada en BBDD.
 * Esta clase almacena los datos del recurso FHIR Practitioner en formato JSON dentro de la base de datos.
 */
@Entity
@Table(name = "practitioners")
public class FhirPractitionerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Esta columna habria que desnormalizar para almacenar identifier_value
    @Column(name = "identifier_value")
    private String identifierValue;
     */
    @Column(name = "resource_practitioner_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String resourcePractitionerJson;

    /**
     * Relación **OneToOne** con {@link MyUser}, indicando qué usuario está asociado al Practitioner.
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    private MyUser user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourcePractitionerJson() {
        return resourcePractitionerJson;
    }

    /**
     * Estalcene la representación JSON del recurso FHIR Practitioner
     * @param resourcePractitionerJson Nuevo JSON del Practitioner.
     */
    public void setResourcePractitionerJson(String resourcePractitionerJson) {
        this.resourcePractitionerJson = resourcePractitionerJson;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }
}