package com.example.memorial_application.domain.service;

import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.model.MemorialApplicationLikes;
import com.example.memorial_application.domain.model.MemorialApplicationLikesId;
import com.example.memorial_application.domain.mapper.MemorialApplicationLikesMapper;
import com.example.memorial_application.domain.repository.MemorialApplicationLikesRepository;
import com.example.memorial_application.domain.exception.AlreadyMemorialApplicationLikesException;
import com.example.memorial_application.domain.exception.NotFoundMemorialApplicationLikesException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemorialApplicationLikesService {
  private final MemorialApplicationLikesRepository memorialApplicationLikesRepository;
  private final MemorialApplicationLikesMapper memorialApplicationLikesMapper;

  private final MemorialApplicationFinder finder;

  @Transactional
  public void like(String userId, Long memorialApplicationId) {
    MemorialApplicationLikesId memorialApplicationLikesId = memorialApplicationLikesMapper.toMemorialApplicationLikeId(memorialApplicationId, userId);
    boolean existsMemorialApplicationLikes = memorialApplicationLikesRepository.existsById(memorialApplicationLikesId);
    if (existsMemorialApplicationLikes) {
      throw AlreadyMemorialApplicationLikesException.getInstance();
    }
    saveMemorialApplicationLike(memorialApplicationLikesId, memorialApplicationId);
  }

  @Transactional
  public void unlike(String userId, Long memorialApplicationId) {
    MemorialApplicationLikesId memorialApplicationLikesId = memorialApplicationLikesMapper.toMemorialApplicationLikeId(memorialApplicationId, userId);
    MemorialApplicationLikes memorialApplicationLikes = memorialApplicationLikesRepository.findById(memorialApplicationLikesId)
            .orElseThrow(NotFoundMemorialApplicationLikesException::getInstance);
    deleteMemorialApplicationLike(memorialApplicationLikes, memorialApplicationId);
  }

  private MemorialApplicationLikes deleteMemorialApplicationLike(MemorialApplicationLikes memorialApplicationLike, Long memorialApplicationId) {
    memorialApplicationLikesRepository.delete(memorialApplicationLike);

    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    memorialApplication.decrementLikes();

    return memorialApplicationLike;
  }

  private MemorialApplicationLikes saveMemorialApplicationLike(MemorialApplicationLikesId memorialApplicationLikesId, Long memorialApplicationId) {

    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);

    MemorialApplicationLikes memorialApplicationLike = memorialApplicationLikesMapper.toMemorialApplicationLike(
            memorialApplicationLikesId,
            memorialApplication
    );
    memorialApplicationLikesRepository.save(memorialApplicationLike);

    memorialApplication.incrementLikes();
    return memorialApplicationLike;
  }

}
