package com.example.memorial_application.domain.mapper;

import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.model.MemorialApplicationState;
import com.example.memorial_application.domain.dto.response.MemorialApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import windeath44.server.application.avro.MemorialApplicationAvroSchema;

import java.time.LocalDate;
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


  public List<MemorialApplicationResponse> toMemorialApplicationPageListResponse(Slice<MemorialApplication> memorialApplicationSlice, String viewerId) {
    return memorialApplicationSlice.getContent()
            .stream()
            .map(memorialApplication -> toMemorialApplicationResponse(memorialApplication, viewerId))
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

  public List<MemorialApplicationResponse> toMemorialApplicationListResponse(Slice<MemorialApplication> memorialApplicationSlice, String viewerId) {
    return memorialApplicationSlice.getContent()
            .stream()
            .map(memorialApplication -> toMemorialApplicationResponse(memorialApplication, viewerId))
            .toList();
  }

  public MemorialApplicationResponse toMemorialApplicationResponse(MemorialApplication memorialApplication, String viewerId) {
    String userId = memorialApplication.getUserId();
    Long characterId = memorialApplication.getCharacterId();
    String content = memorialApplication.getContent();
    LocalDate createdAt = memorialApplication.getCreatedAt();
    MemorialApplicationState state = memorialApplication.getState();
    Long likes = memorialApplication.getLikes();
    Long memorialApplicationId = memorialApplication.getMemorialApplicationId();
    boolean didUserLiked = viewerId != null && memorialApplication.didUserLiked(viewerId);
    String rejectedReason = (state == MemorialApplicationState.REJECTED && memorialApplication.getRejectedReason() != null)
        ? memorialApplication.getReason()
        : null;

    return new MemorialApplicationResponse(userId, characterId, memorialApplicationId, content, createdAt, state, likes, didUserLiked, rejectedReason);
  }
}
