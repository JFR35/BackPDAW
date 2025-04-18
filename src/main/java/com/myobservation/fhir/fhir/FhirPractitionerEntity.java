package com.myobservation.fhir.fhir;

import com.myobservation.auth.entity.MyUser;
import jakarta.persistence.*;
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

    /**
     * Relacion OneToOne entre FhirPractitionerEntity y @JoingColum(name= "user_id)
     * define la relacion con la entidad MyUser
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