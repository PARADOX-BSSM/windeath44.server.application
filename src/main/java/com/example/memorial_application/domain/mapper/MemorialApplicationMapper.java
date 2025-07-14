package com.example.memorial_application.domain.mapper;


import com.example.avro.MemorialApplicationAvroSchema;
import com.example.avro.MemorialAvroSchema;
import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.model.MemorialApplicationState;
import com.example.memorial_application.domain.dto.response.MemorialApplicationListResponse;
import com.example.memorial_application.domain.dto.response.MemorialApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemorialApplicationMapper {

  public MemorialApplication toMemorialApplication(String userId, Long characterId, String content) {
    return MemorialApplication.builder()
            .userId(userId)
            .characterId(characterId)
            .content(content)
            .state(MemorialApplicationState.PENDING)
            .build();

  }

  public MemorialApplicationListResponse toMemorialApplicationListResponse(MemorialApplication memorialApplication) {
    Long memorialApplicationId = memorialApplication.getMemorialApplicationId();
    String userId = memorialApplication.getUserId();
    Long characterId = memorialApplication.getCharacterId();
    String content = memorialApplication.getContent();
    LocalDate createdAt = memorialApplication.getCreatedAt();
    MemorialApplicationState state = memorialApplication.getState();
    Long likes = memorialApplication.getLikes();

    return new MemorialApplicationListResponse(userId, characterId, memorialApplicationId, content, createdAt, state, likes);
  }

  public MemorialApplicationResponse toMemorialApplicationResponse(MemorialApplication memorialApplication, boolean userDidLike) {
    String userId = memorialApplication.getUserId();
    Long characterId = memorialApplication.getCharacterId();
    String content = memorialApplication.getContent();
    LocalDate createdAt = memorialApplication.getCreatedAt();
    MemorialApplicationState state = memorialApplication.getState();
    Long likes = memorialApplication.getLikes();

    return new MemorialApplicationResponse(userId, characterId, content, createdAt, state, likes, userDidLike);
  }

  public List<MemorialApplicationListResponse> toMemorialApplicationPageListResponse(Slice<MemorialApplication> memorialApplicationSlice) {
    return memorialApplicationSlice.getContent()
            .stream()
            .map(this::toMemorialApplicationListResponse)
            .toList();
  }

  public MemorialApplicationAvroSchema toMemorialApplicationAvroSchema(MemorialApplication memorialApplication, String userId) {
    Long memorialApplicationId = memorialApplication.getMemorialApplicationId();
    String applicantId = memorialApplication.getUserId();
    String content = memorialApplication.getContent();
    Long characterId = memorialApplication.getCharacterId();

    return MemorialApplicationAvroSchema.newBuilder()
            .setMemorialApplicationId(memorialApplicationId)
            .setApplicantId(applicantId)
            .setApproverId(userId)
            .setContent(content)
            .setCharacterId(characterId)
            .build();
  }
}
