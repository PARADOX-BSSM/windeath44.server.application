package com.example.memorial_application.domain.exception;

import com.example.memorial_application.global.error.exception.ErrorCode;
import com.example.memorial_application.global.error.exception.GlobalException;

public class AlreadyMemorialApplicationException extends GlobalException {
  public AlreadyMemorialApplicationException() {
    super(ErrorCode.ALREADY_MEMORIAL_APPLICATION);
  }

  static class Holder {
    private static final AlreadyMemorialApplicationException INSTANCE = new AlreadyMemorialApplicationException();
  }
  public static AlreadyMemorialApplicationException getInstance() {
    return Holder.INSTANCE;
  }
}
