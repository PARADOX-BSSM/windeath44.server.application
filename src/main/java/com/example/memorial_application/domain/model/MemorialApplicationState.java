package com.example.memorial_application.domain.model;

public enum MemorialApplicationState {
  PENDING, APPROVED, REJECTED;

  public static MemorialApplicationState isMemorializing(Integer state) {
    return switch (state) {
      case 1 -> APPROVED;
      case 2 -> REJECTED;
      case 3 -> PENDING;
      default -> null;
    };
  }
}
