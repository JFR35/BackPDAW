package com.myobservation;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModuleStructureTest {
    @Test
    void verificarEstructuraModular() {
        ApplicationModules modules = ApplicationModules.of(MyobservationApplication.class);
        modules.verify(); // Esto verifica que no haya dependencias cruzadas indebidas
    }
}
