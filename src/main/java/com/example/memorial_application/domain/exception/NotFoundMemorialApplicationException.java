package com.example.memorial_application.domain.exception;

import com.example.memorial_application.global.error.exception.ErrorCode;
import com.example.memorial_application.global.error.exception.GlobalException;

public class NotFoundMemorialApplicationException extends GlobalException {
  public NotFoundMemorialApplicationException() {
    super(ErrorCode.MEMORIAL_APPLICATION_NOT_FOUND);
  }
  private static class Holder {
    private static final NotFoundMemorialApplicationException INSTANCE = new NotFoundMemorialApplicationException();
  }

  public static NotFoundMemorialApplicationException getInstance() {
    return Holder.INSTANCE;
  }
}
