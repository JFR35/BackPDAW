package com.myobservation;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.docs.Documenter;

public class ModuleDocumentationTest {

    @Test
    void generarDocumentacionModular() {
        new Documenter(MyobservationApplication.class)
                .writeDocumentation(); // genera la doc en /target/spring-modulith-docs
    }
}
