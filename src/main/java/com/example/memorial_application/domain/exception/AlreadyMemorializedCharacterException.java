package com.example.memorial_application.domain.exception;

import com.example.memorial_application.global.error.exception.ErrorCode;
import com.example.memorial_application.global.error.exception.GlobalException;

public class AlreadyMemorializedCharacterException extends GlobalException {
  public AlreadyMemorializedCharacterException() {
    super(ErrorCode.ALREADY_MEMORIALIZED_CHARACTER);
  }
  private static class Holder {
    private static final AlreadyMemorializedCharacterException INSTANCE = new AlreadyMemorializedCharacterException();
  }

  public static AlreadyMemorializedCharacterException getInstance() {
    return Holder.INSTANCE;
  }
}
