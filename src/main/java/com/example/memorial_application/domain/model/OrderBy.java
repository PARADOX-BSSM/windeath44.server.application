package com.example.memorial_application.domain.model;

public enum OrderBy {
  RECENT, OLD, POPULAR;

  public static OrderBy fromString(String value) {
    if (value == null || value.isEmpty()) {
      return RECENT; // 기본값
    }
    
    return switch (value.toLowerCase()) {
      case "recent" -> RECENT;
      case "old" -> OLD;
      case "popular" -> POPULAR;
      default -> RECENT;
    };
  }
}
