package com.example.memorial_application.domain.dto.response;

import com.example.memorial_application.domain.model.MemorialApplicationState;

import java.time.LocalDateTime;

public record MemorialApplicationListResponse (
        // 유저 정보
        String userId,
        // 캐릭터 정보
        Long characterId,
        //신청 정보
        Long memorialApplicationId,
        String content,
        LocalDateTime createdAt,
        MemorialApplicationState state,
        Long likes
) {
}