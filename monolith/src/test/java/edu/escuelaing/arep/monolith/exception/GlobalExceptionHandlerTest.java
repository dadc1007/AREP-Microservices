package edu.escuelaing.arep.monolith.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import edu.escuelaing.arep.monolith.dto.response.ErrorResponse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleApiExceptionShouldReturnMappedError() {
        ApiException ex = new ApiException(ErrorCode.USER_NOT_FOUND);

        ResponseEntity<ErrorResponse> response = handler.handleApiException(ex);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("USER_NOT_FOUND", response.getBody().error());
        assertEquals("User not found", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleGeneralExceptionShouldReturnInternalServerError() {
        Exception ex = new Exception("boom");

        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().error());
        assertEquals("boom", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }
}
