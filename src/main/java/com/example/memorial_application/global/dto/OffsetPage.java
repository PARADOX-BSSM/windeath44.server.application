package com.example.memorial_application.global.dto;

import java.util.List;

public record OffsetPage<T> (
        List<T> values,
        int total
) {
}
