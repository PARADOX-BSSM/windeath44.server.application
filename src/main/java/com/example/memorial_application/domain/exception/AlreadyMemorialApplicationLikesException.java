package com.example.memorial_application.domain.exception;

import com.example.memorial_application.global.error.exception.ErrorCode;
import com.example.memorial_application.global.error.exception.GlobalException;

public class AlreadyMemorialApplicationLikesException extends GlobalException {
  public AlreadyMemorialApplicationLikesException() {
    super(ErrorCode.ALREADY_MEMORIAL_APPLICATION_LIKES);
  }

  private static class Holder {
    private static final AlreadyMemorialApplicationLikesException INSTANCE = new AlreadyMemorialApplicationLikesException();
  }

  public static AlreadyMemorialApplicationLikesException getInstance() {
    return Holder.INSTANCE;
  }

}
