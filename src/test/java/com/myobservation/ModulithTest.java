package com.myobservation;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModulithTest {

    /**
     * Test para comprobar la creación de paquetes en la estructura modular
     */
    @Test
    void createApplicationModuleModel() {
        ApplicationModules modules = ApplicationModules.of(MyobservationApplication.class);
        modules.forEach(System.out::println);
        modules.verify();
    }
}
