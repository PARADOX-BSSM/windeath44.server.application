package com.example.memorial_application.domain.dto.request;

public record MemorialApplicationRequest(
        Long characterId,
        String content
) {
}