package com.example.memorial_application.global.dto;

public record ResponseDto<T> (
        String message,
        T data
) {
}
