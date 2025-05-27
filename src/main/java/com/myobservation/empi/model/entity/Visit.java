// src/main/java/com/myobservation/empi/model/entity/Visit.java
package com.myobservation.empi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID; // Importa UUID

@Entity
@Table(name = "visits") // Es buena práctica dar un nombre explícito a la tabla
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key de la DB

    @Column(unique = true, nullable = false)
    private String visitUuid; // Usaremos UUID para la lógica de negocio y exposición externa

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading para evitar cargar objetos Patient completos
    @JoinColumn(name = "patient_id", nullable = false) // Asegúrate de que el ID del paciente no sea nulo
    private PatientMasterIndex patient;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading
    @JoinColumn(name = "practitioner_id", nullable = false) // Asegúrate de que el ID del profesional no sea nulo
    private PractitionerMasterIndex practitioner;

    @Column(nullable = false)
    private LocalDateTime visitDate;

    // compositionId será un string, pero podría haber múltiples observaciones en una visita.
    // Si una visita solo tiene UNA medición de presión arterial, está bien.
    // Si puede tener varias, considera una relación @OneToMany con una entidad 'Observation'
    // o un campo 'compositionIds' (List<String>) si solo quieres IDs.
    private String bloodPressureCompositionId; // Más específico que 'compositionId'

    @PrePersist
    public void generateUuidAndSetDate() {
        if (this.visitUuid == null) {
            this.visitUuid = UUID.randomUUID().toString();
        }
        if (this.visitDate == null) {
            this.visitDate = LocalDateTime.now();
        }
    }

    // Getters and setters (asegúrate de que los nombres coincidan con los campos)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVisitUuid() { return visitUuid; } // Getter para visitUuid
    public void setVisitUuid(String visitUuid) { this.visitUuid = visitUuid; } // Setter para visitUuid
    public PatientMasterIndex getPatient() { return patient; }
    public void setPatient(PatientMasterIndex patient) { this.patient = patient; }
    public PractitionerMasterIndex getPractitioner() { return practitioner; }
    public void setPractitioner(PractitionerMasterIndex practitioner) { this.practitioner = practitioner; }
    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }
    public String getBloodPressureCompositionId() { return bloodPressureCompositionId; } // Getter específico
    public void setBloodPressureCompositionId(String bloodPressureCompositionId) { this.bloodPressureCompositionId = bloodPressureCompositionId; } // Setter específico
}