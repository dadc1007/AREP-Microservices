package edu.escuelaing.arep.monolith.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ApiExceptionTest {

    @Test
    void constructorShouldExposeErrorCodeAndMessage() {
        ApiException exception = new ApiException(ErrorCode.POST_TOO_LONG);

        assertEquals(ErrorCode.POST_TOO_LONG, exception.getErrorCode());
        assertEquals("Post exceeds 140 characters", exception.getMessage());
    }
}
