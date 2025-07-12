package com.example.memorial_application.global.dto;

import java.util.List;

public record CursorPage<T> (
        List<T> values,
        Boolean hasNext
) {
}
