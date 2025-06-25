package com.myobservation;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;


public class ModuleStructureTest {

    // Para verificar la estructura modular de la aplicación
    @Test
    void verificarEstructuraModular() {
        ApplicationModules modules = ApplicationModules.of(MyobservationApplication.class);
        modules.verify();
    }
}
