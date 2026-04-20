package edu.escuelaing.arep.monolith.dto.response;

import lombok.Builder;

@Builder
public record PostResponse(
        Long id,
        String content,
        String username) {
}
