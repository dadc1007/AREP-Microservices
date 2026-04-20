package edu.escuelaing.arep.monolith.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp) {
}
