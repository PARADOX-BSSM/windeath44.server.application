package com.example.memorial_application.global.mapper.dto;

public record ResponseDto<T> (
        String message,
        T data
) {
}
