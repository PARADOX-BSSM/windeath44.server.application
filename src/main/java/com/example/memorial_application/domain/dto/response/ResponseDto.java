package com.example.memorial_application.domain.dto.response;

import java.util.List;

public record ResponseDto<T> (
        String message,
        T data
) {

}
