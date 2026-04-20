package edu.escuelaing.arep.monolith.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    STREAM_NOT_FOUND(HttpStatus.NOT_FOUND, "Stream not found"),
    POST_TOO_LONG(HttpStatus.BAD_REQUEST, "Post exceeds 140 characters"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "User with this email already exists"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Username already exists");

    private final HttpStatus httpStatus;
    private final String message;
}
