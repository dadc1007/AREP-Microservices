package edu.escuelaing.arep.monolith.exception;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.escuelaing.arep.monolith.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        ErrorCode code = e.getErrorCode();

        ErrorResponse error = ErrorResponse.builder()
                .status(code.getHttpStatus().value())
                .error(code.name())
                .message(code.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse error = ErrorResponse.builder()
                .status(500)
                .error("INTERNAL_SERVER_ERROR")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(500).body(error);
    }
}
