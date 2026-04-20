package edu.escuelaing.arep.monolith.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorCodeTest {

    @Test
    void userNotFoundShouldHaveExpectedStatusAndMessage() {
        assertEquals(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND.getHttpStatus());
        assertEquals("User not found", ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void usernameAlreadyExistsShouldHaveExpectedStatusAndMessage() {
        assertEquals(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS.getHttpStatus());
        assertEquals("Username already exists", ErrorCode.USERNAME_ALREADY_EXISTS.getMessage());
    }
}
