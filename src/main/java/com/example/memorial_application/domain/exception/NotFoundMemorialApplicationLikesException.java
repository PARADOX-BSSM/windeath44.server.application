package com.example.memorial_application.domain.exception;

import com.example.memorial_application.global.error.exception.ErrorCode;
import com.example.memorial_application.global.error.exception.GlobalException;

public class NotFoundMemorialApplicationLikesException extends GlobalException {
  public NotFoundMemorialApplicationLikesException() {
    super(ErrorCode.MEMORIAL_APPLICATION_LIKES_NOT_FOUND);
  }

  private static class Holder {
    private static final NotFoundMemorialApplicationLikesException INSTANCE = new NotFoundMemorialApplicationLikesException();
  }

  public static NotFoundMemorialApplicationLikesException getInstance() {
    return Holder.INSTANCE;
  }
}
