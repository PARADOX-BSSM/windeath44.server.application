package com.example.memorial_application.global.dto;

import com.example.memorial_application.domain.dto.response.MemorialApplicationResponse;

import java.util.List;

public record OffsetPage<T> (
        List<T> values,
        int total
) {
    public static OffsetPage<MemorialApplicationResponse> of(List<MemorialApplicationResponse> content, int total) {
        return new OffsetPage<>(content, total);
    }
}
