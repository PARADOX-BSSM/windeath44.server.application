package com.example.memorial_application.domain.mapper;

import com.example.memorial_application.domain.model.MemorialApplicationLikes;
import com.example.memorial_application.domain.model.MemorialApplicationLikesId;
import org.springframework.stereotype.Component;

@Component
public class MemorialApplicationLikesMapper {
  public MemorialApplicationLikesId toMemorialApplicationLikeId(Long memorialApplicationId, String userId) {
    return new MemorialApplicationLikesId(memorialApplicationId, userId);
  }

  public MemorialApplicationLikes toMemorialApplicationLike(MemorialApplicationLikesId memorialApplicationLikesId) {
    return new MemorialApplicationLikes(memorialApplicationLikesId);
  }
}
