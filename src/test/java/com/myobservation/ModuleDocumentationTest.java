package com.myobservation;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.docs.Documenter;

/**
 * Clase de prueba para generar documentacion con la
 * herramienta Spring Modulith
 */
public class ModuleDocumentationTest {

    @Test
    void generarDocumentacionModular() {
        new Documenter(MyobservationApplication.class)
                .writeDocumentation(); // genera la doc en /target/spring-modulith-docs
    }
}
