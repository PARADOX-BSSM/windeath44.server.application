package com.example.memorial_application.domain.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemorialApplicationRequest(
        @NotNull(message = "characterId is null")
        Long characterId,
        @NotNull(message = "content is null")
        String content
) {
}