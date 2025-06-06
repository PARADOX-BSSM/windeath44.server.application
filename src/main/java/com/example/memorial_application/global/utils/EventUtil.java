package com.example.memorial_application.global.utils;

import com.example.avro.MemorialAvroSchema;
import com.example.memorial_application.domain.model.MemorialApplication;


public class EventUtil {
  public static MemorialAvroSchema memorialAvroSchema(MemorialApplication memorialApplication, String userId) {
    String applicantId = memorialApplication.getUserId();
    String content = memorialApplication.getContent();
    Long characterId = memorialApplication.getCharacterId();

    return MemorialAvroSchema.newBuilder()
            .setApplicantId(applicantId)
            .setApproverId(userId)
            .setContent(content)
            .setCharacterId(characterId)
            .build();
  }
}
