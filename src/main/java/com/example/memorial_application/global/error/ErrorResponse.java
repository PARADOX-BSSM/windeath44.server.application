package com.example.memorial_application.global.error;

import com.example.memorial_application.global.error.exception.ErrorCode;

public record ErrorResponse (
        int status,
        String message
) {
  public ErrorResponse(ErrorCode errorCode){
    this(
            errorCode.getStatus(),
            errorCode.getMessage()
    );
  }
}
