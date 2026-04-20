package edu.escuelaing.arep.monolith;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MonolithApplicationMainTest {

    @Test
    void mainClassShouldBeInstantiable() {
        MonolithApplication app = new MonolithApplication();

        assertNotNull(app);
    }
}
