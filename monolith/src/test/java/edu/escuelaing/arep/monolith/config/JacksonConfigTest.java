package edu.escuelaing.arep.monolith.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class JacksonConfigTest {

    @Test
    void objectMapperShouldDisableWriteDatesAsTimestamps() {
        JacksonConfig config = new JacksonConfig();

        ObjectMapper mapper = config.objectMapper();

        assertNotNull(mapper);
        assertFalse(mapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }
}
