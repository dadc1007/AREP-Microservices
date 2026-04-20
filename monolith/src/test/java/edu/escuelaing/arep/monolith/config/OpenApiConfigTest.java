package edu.escuelaing.arep.monolith.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    @Test
    void shouldInstantiateOpenApiConfig() {
        OpenApiConfig config = new OpenApiConfig();

        assertNotNull(config);
    }
}
