package com.example.memorial_application.domain.service;

import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.mapper.MemorialApplicationLikesMapper;
import com.example.memorial_application.domain.mapper.MemorialApplicationMapper;
import com.example.memorial_application.domain.model.MemorialApplicationState;
import com.example.memorial_application.domain.model.OrderBy;
import com.example.memorial_application.domain.repository.MemorialApplicationLikesRepository;
import com.example.memorial_application.domain.repository.MemorialApplicationRepository;
import com.example.memorial_application.domain.dto.response.MemorialApplicationResponse;
import com.example.memorial_application.global.dto.CursorPage;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
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

  public MemorialApplicationResponse findById(Long memorialApplicationId, String userId) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    MemorialApplicationResponse memorialApplicationResponse = memorialApplicationMapper.toMemorialApplicationResponse(memorialApplication, userId);
    return memorialApplicationResponse;
  }

  public CursorPage<MemorialApplicationResponse> findByCursor(Long cursorId, int size, Integer memorizingCode, String userId, OrderBy orderBy) {
    Pageable pageable = PageRequest.of(0, size);

    MemorialApplicationState memorialzing = MemorialApplicationState.isMemorializing(memorizingCode);
    Slice<MemorialApplication> memorialApplicationSlice = getMemorialApplications(cursorId, memorialzing, pageable, orderBy);

    List<MemorialApplicationResponse> memorialApplicationsList = memorialApplicationMapper.toMemorialApplicationPageListResponse(memorialApplicationSlice, userId);
    return new CursorPage<>(memorialApplicationsList, memorialApplicationSlice.hasNext());
  }

  private Slice<MemorialApplication> getMemorialApplications(Long cursorId, MemorialApplicationState memorialzing, Pageable pageable, OrderBy orderBy) {
    Slice<MemorialApplication> memorialApplicationSlice;
    
    if (memorialzing == null) {
      // 상태 필터 없음
      memorialApplicationSlice = switch (orderBy) {
        case RECENT -> cursorId == null
                ? memorialApplicationRepository.findPageableRecent(pageable)
                : memorialApplicationRepository.findPageableByCursorRecent(cursorId, pageable);
        case OLD -> cursorId == null
                ? memorialApplicationRepository.findPageableOld(pageable)
                : memorialApplicationRepository.findPageableByCursorOld(cursorId, pageable);
        case POPULAR -> cursorId == null
                ? memorialApplicationRepository.findPageablePopular(pageable)
                : memorialApplicationRepository.findPageableByCursorPopular(cursorId, pageable);
      };
    } else {
      // 상태 필터 있음
      memorialApplicationSlice = switch (orderBy) {
        case RECENT -> cursorId == null
                ? memorialApplicationRepository.findPageableByMemorizingRecent(pageable, memorialzing)
                : memorialApplicationRepository.findPageableByCursorAndMemorizingRecent(cursorId, pageable, memorialzing);
        case OLD -> cursorId == null
                ? memorialApplicationRepository.findPageableByMemorizingOld(pageable, memorialzing)
                : memorialApplicationRepository.findPageableByCursorAndMemorizingOld(cursorId, pageable, memorialzing);
        case POPULAR -> cursorId == null
                ? memorialApplicationRepository.findPageableByMemorizingPopular(pageable, memorialzing)
                : memorialApplicationRepository.findPageableByCursorAndMemorizingPopular(cursorId, pageable, memorialzing);
      };
    }
    
    return memorialApplicationSlice;
  }

  public CursorPage<MemorialApplicationResponse> findByCharacterId(Long characterId, Long cursorId, int size, String userId, OrderBy orderBy) {
    Pageable pageable = PageRequest.of(0, size);
    
    Slice<MemorialApplication> memorialApplicationSlice = switch (orderBy) {
      case RECENT -> cursorId == null
              ? memorialApplicationRepository.findByCharacterIdRecent(characterId, pageable)
              : memorialApplicationRepository.findByCharacterIdAndCursorIdRecent(characterId, cursorId, pageable);
      case OLD -> cursorId == null
              ? memorialApplicationRepository.findByCharacterIdOld(characterId, pageable)
              : memorialApplicationRepository.findByCharacterIdAndCursorIdOld(characterId, cursorId, pageable);
      case POPULAR -> cursorId == null
              ? memorialApplicationRepository.findByCharacterIdPopular(characterId, pageable)
              : memorialApplicationRepository.findByCharacterIdAndCursorIdPopular(characterId, cursorId, pageable);
    };

    List<MemorialApplicationResponse> memorialApplicationResponse = memorialApplicationMapper.toMemorialApplicationListResponse(memorialApplicationSlice, userId);
    return new CursorPage<>(memorialApplicationResponse, memorialApplicationSlice.hasNext());
  }

  public CursorPage<MemorialApplicationResponse> findMyApplicationByCursor(String userId, Long cursorId, int size, String orderByParam) {
    Pageable pageable = PageRequest.of(0, size + 1);
    OrderBy orderBy = OrderBy.fromString(orderByParam);

    Slice<MemorialApplication> memorialApplicationSlice = switch (orderBy) {
      case RECENT -> cursorId == null
              ? memorialApplicationRepository.findMyApplicationPageableRecent(userId, pageable)
              : memorialApplicationRepository.findMyApplicationPageableByCursorIdRecent(userId, cursorId, pageable);
      case OLD -> cursorId == null
              ? memorialApplicationRepository.findMyApplicationPageableOld(userId, pageable)
              : memorialApplicationRepository.findMyApplicationPageableByCursorIdOld(userId, cursorId, pageable);
      case POPULAR -> cursorId == null
              ? memorialApplicationRepository.findMyApplicationPageablePopular(userId, pageable)
              : memorialApplicationRepository.findMyApplicationPageableByCursorIdPopular(userId, cursorId, pageable);
    };

    List<MemorialApplicationResponse> memorialApplicationsList = memorialApplicationMapper.toMemorialApplicationPageListResponse(memorialApplicationSlice, userId);

    return new CursorPage<>(memorialApplicationsList, memorialApplicationSlice.hasNext());
  }
}
