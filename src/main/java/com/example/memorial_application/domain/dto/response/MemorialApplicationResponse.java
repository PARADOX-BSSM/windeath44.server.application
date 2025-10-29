package com.example.memorial_application.domain.dto.response;

import com.example.memorial_application.domain.model.MemorialApplicationState;

import java.time.LocalDate;

public record MemorialApplicationResponse(
        // 유저 정보
        String userId,
        // 캐릭터 정보
        Long characterId,
        //신청 정보
        Long memorialApplicationId,
        String content,
        LocalDate createdAt,
        MemorialApplicationState state,
        Long likes,
        boolean didUserLiked
) {
}