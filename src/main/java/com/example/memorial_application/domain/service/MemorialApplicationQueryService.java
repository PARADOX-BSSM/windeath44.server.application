package com.example.memorial_application.domain.service;

import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.model.MemorialApplicationLikesId;
import com.example.memorial_application.domain.mapper.MemorialApplicationLikesMapper;
import com.example.memorial_application.domain.mapper.MemorialApplicationMapper;
import com.example.memorial_application.domain.repository.MemorialApplicationLikesRepository;
import com.example.memorial_application.domain.repository.MemorialApplicationRepository;
import com.example.memorial_application.domain.dto.response.MemorialApplicationListResponse;
import com.example.memorial_application.domain.dto.response.MemorialApplicationResponse;
import com.example.memorial_application.domain.exception.NotFoundMemorialApplicationException;
import com.example.memorial_application.global.mapper.dto.CursorPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemorialApplicationQueryService  {
  private final MemorialApplicationRepository memorialApplicationRepository;
  private final MemorialApplicationMapper memorialApplicationMapper;
  private final MemorialApplicationFinder finder;

  private final MemorialApplicationLikesRepository memorialApplicationLikesRepository;
  private final MemorialApplicationLikesMapper memorialApplicationLikesMapper;

  public List<MemorialApplicationListResponse> findAll() {
    List<MemorialApplicationListResponse> memorialApplicationList = getMemorialApplicationList();
    return memorialApplicationList;
  }

  private List<MemorialApplicationListResponse> getMemorialApplicationList() {
    List<MemorialApplicationListResponse> memorialApplicationResponseList = memorialApplicationRepository.findAllSortByLikes()
            .stream()
            .map(memorialApplicationMapper::toMemorialApplicationListResponse)
            .toList();
    return memorialApplicationResponseList;
  }

  public MemorialApplicationResponse findById(Long memorialApplicationId, String userId) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    boolean userDidLikes = didUserLike(userId, memorialApplicationId);
    MemorialApplicationResponse memorialApplicationResponse = memorialApplicationMapper.toMemorialApplicationResponse(memorialApplication, userDidLikes);
    return memorialApplicationResponse;
  }

  private boolean didUserLike(String userId, Long memorialApplicationId) {
    MemorialApplicationLikesId memorialApplicationLikesId = memorialApplicationLikesMapper.toMemorialApplicationLikeId(memorialApplicationId, userId);
    boolean userDidLikes = memorialApplicationLikesRepository.existsById(memorialApplicationLikesId);
    return userDidLikes;
  }


  public CursorPage<MemorialApplicationListResponse> findByCursor(Long cursorId, int size) {
    Pageable pageable = PageRequest.of(0, size + 1);

    Slice<MemorialApplication> memorialApplicationSlice = cursorId == null
            ? memorialApplicationRepository.findPageable(pageable)
            : memorialApplicationRepository.findPageableByCursor(cursorId, pageable);

    List<MemorialApplicationListResponse> memorialApplicationsList = memorialApplicationMapper.toMemorialApplicationPageListResponse(memorialApplicationSlice);
    return new CursorPage<>(memorialApplicationsList, memorialApplicationSlice.hasNext());
  }

  public MemorialApplicationResponse findByCharacterId(Long characterId, String userId) {
    MemorialApplication memorialApplication = memorialApplicationRepository.findByCharacterId(characterId)
            .orElseThrow(NotFoundMemorialApplicationException::getInstance);
    boolean userDidLikes = userId != null && didUserLike(userId, memorialApplication.getMemorialApplicationId());
    MemorialApplicationResponse memorialApplicationResponse = memorialApplicationMapper.toMemorialApplicationResponse(memorialApplication, userDidLikes);
    return memorialApplicationResponse;
  }

  public CursorPage<MemorialApplicationListResponse> findMyApplicationByCursor(String userId, Long cursorId, int size) {
    Pageable pageable = PageRequest.of(0, size + 1);

    Slice<MemorialApplication> memorialApplicationSlice = cursorId == null
            ? memorialApplicationRepository.findMyApplicationPageable(userId, pageable)
            : memorialApplicationRepository.findMyApplicationPageableByCursorId(userId, cursorId, pageable);

    List<MemorialApplicationListResponse> memorialApplicationsList = memorialApplicationMapper.toMemorialApplicationPageListResponse(memorialApplicationSlice);

    return new CursorPage<>(memorialApplicationsList, memorialApplicationSlice.hasNext());


  }
}
