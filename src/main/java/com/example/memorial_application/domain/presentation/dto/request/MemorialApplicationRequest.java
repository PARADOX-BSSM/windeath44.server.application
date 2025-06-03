package com.example.memorial_application.domain.presentation.dto.request;

public record MemorialApplicationRequest(
        Long characterId,
        String content
) {
}
