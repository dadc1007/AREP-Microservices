package edu.escuelaing.arep.monolith.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class SecurityErrorCodeTest {

    @Test
    void unauthorizedShouldHaveExpectedStatusAndMessage() {
        assertEquals(HttpStatus.UNAUTHORIZED, SecurityErrorCode.UNAUTHORIZED.getHttpStatus());
        assertEquals("Unauthorized", SecurityErrorCode.UNAUTHORIZED.getMessage());
    }

    @Test
    void forbiddenShouldHaveExpectedStatusAndMessage() {
        assertEquals(HttpStatus.FORBIDDEN, SecurityErrorCode.FORBIDDEN.getHttpStatus());
        assertEquals("Forbidden", SecurityErrorCode.FORBIDDEN.getMessage());
    }
}
