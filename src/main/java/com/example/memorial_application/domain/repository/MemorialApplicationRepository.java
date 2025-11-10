package com.example.memorial_application.domain.repository;

import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.model.MemorialApplicationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemorialApplicationRepository extends JpaRepository<MemorialApplication, Long> {

  @Query("select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "left join fetch m.memorialApplicationLikes " +
         "where m.memorialApplicationId = :id")
  Optional<MemorialApplication> findByIdWithFetch(@Param("id") Long id);

  @Modifying
  @Query(value = "update MemorialApplication m set m.state = 'REJECTED' where m.characterId = :characterId and m.memorialApplicationId != :memorialApplicationId")
  void updateStateToRejectedByCharacterId(@Param("memorialApplicationId") Long memorialApplicationId, @Param("characterId") Long characterId);


  Optional<MemorialApplication> findByUserIdAndCharacterId(String applicantId, Long characterId);

  Boolean existsByUserIdAndCharacterId(String userId, Long characterId);

  // ===== 전체 조회 - 정렬별 =====
  // RECENT (최근순 - desc)
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findPageableRecent(Pageable pageable);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.memorialApplicationId < :cursorId " +
         "order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findPageableByCursorRecent(@Param("cursorId") Long cursorId, Pageable pageable);

  // OLD (오래된순 - asc)
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findPageableOld(Pageable pageable);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.memorialApplicationId > :cursorId " +
         "order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findPageableByCursorOld(@Param("cursorId") Long cursorId, Pageable pageable);

  // POPULAR (인기순 - likes desc)
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Slice<MemorialApplication> findPageablePopular(Pageable pageable);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.memorialApplicationId < :cursorId " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Slice<MemorialApplication> findPageableByCursorPopular(@Param("cursorId") Long cursorId, Pageable pageable);

  // ===== 상태별 조회 - 정렬별 =====
  // RECENT
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state " +
         "order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findPageableByMemorizingRecent(Pageable pageable, @Param("state") MemorialApplicationState state);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state and m.memorialApplicationId < :cursorId " +
         "order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findPageableByCursorAndMemorizingRecent(@Param("cursorId") Long cursorId, Pageable pageable, @Param("state") MemorialApplicationState state);

  // OLD
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state " +
         "order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findPageableByMemorizingOld(Pageable pageable, @Param("state") MemorialApplicationState state);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state and m.memorialApplicationId > :cursorId " +
         "order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findPageableByCursorAndMemorizingOld(@Param("cursorId") Long cursorId, Pageable pageable, @Param("state") MemorialApplicationState state);

  // POPULAR
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Slice<MemorialApplication> findPageableByMemorizingPopular(Pageable pageable, @Param("state") MemorialApplicationState state);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state and m.memorialApplicationId < :cursorId " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Slice<MemorialApplication> findPageableByCursorAndMemorizingPopular(@Param("cursorId") Long cursorId, Pageable pageable, @Param("state") MemorialApplicationState state);

  // ===== 캐릭터별 조회 - 정렬별 =====
  // RECENT
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.characterId = :characterId " +
         "order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findByCharacterIdRecent(@Param("characterId") Long characterId, Pageable pageable);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.characterId = :characterId and m.memorialApplicationId < :cursorId " +
         "order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findByCharacterIdAndCursorIdRecent(@Param("characterId") Long characterId, @Param("cursorId") Long cursorId, Pageable pageable);

  // OLD
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.characterId = :characterId " +
         "order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findByCharacterIdOld(@Param("characterId") Long characterId, Pageable pageable);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.characterId = :characterId and m.memorialApplicationId > :cursorId " +
         "order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findByCharacterIdAndCursorIdOld(@Param("characterId") Long characterId, @Param("cursorId") Long cursorId, Pageable pageable);

  // POPULAR
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.characterId = :characterId " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Slice<MemorialApplication> findByCharacterIdPopular(@Param("characterId") Long characterId, Pageable pageable);

  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.characterId = :characterId and m.memorialApplicationId < :cursorId " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Slice<MemorialApplication> findByCharacterIdAndCursorIdPopular(@Param("characterId") Long characterId, @Param("cursorId") Long cursorId, Pageable pageable);

  // ===== 내 신청 조회 - 정렬별 =====
  // RECENT
  @Query("select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.userId = :userId " +
         "order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findMyApplicationPageableRecent(@Param("userId") String userId, Pageable pageable);

  @Query("select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.userId = :userId and m.memorialApplicationId < :cursorId " +
         "order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findMyApplicationPageableByCursorIdRecent(@Param("userId") String userId, @Param("cursorId") Long cursorId, Pageable pageable);

  // OLD
  @Query("select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.userId = :userId " +
         "order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findMyApplicationPageableOld(@Param("userId") String userId, Pageable pageable);

  @Query("select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.userId = :userId and m.memorialApplicationId > :cursorId " +
         "order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findMyApplicationPageableByCursorIdOld(@Param("userId") String userId, @Param("cursorId") Long cursorId, Pageable pageable);

  // POPULAR
  @Query("select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.userId = :userId " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Slice<MemorialApplication> findMyApplicationPageablePopular(@Param("userId") String userId, Pageable pageable);

  @Query("select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.userId = :userId and m.memorialApplicationId < :cursorId " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Slice<MemorialApplication> findMyApplicationPageableByCursorIdPopular(@Param("userId") String userId, @Param("cursorId") Long cursorId, Pageable pageable);

  // ===== 오프셋 기반 페이지네이션 =====
  // 전체 조회 - RECENT
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "order by m.memorialApplicationId desc")
  Page<MemorialApplication> findAllWithOffsetRecent(Pageable pageable);

  // 전체 조회 - OLD
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "order by m.memorialApplicationId asc")
  Page<MemorialApplication> findAllWithOffsetOld(Pageable pageable);

  // 전체 조회 - POPULAR
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Page<MemorialApplication> findAllWithOffsetPopular(Pageable pageable);

  // 상태별 조회 - RECENT
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state " +
         "order by m.memorialApplicationId desc")
  Page<MemorialApplication> findByStateWithOffsetRecent(@Param("state") MemorialApplicationState state, Pageable pageable);

  // 상태별 조회 - OLD
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state " +
         "order by m.memorialApplicationId asc")
  Page<MemorialApplication> findByStateWithOffsetOld(@Param("state") MemorialApplicationState state, Pageable pageable);

  // 상태별 조회 - POPULAR
  @Query(value = "select m from MemorialApplication m " +
         "left join fetch m.rejectedReason " +
         "where m.state = :state " +
         "order by m.likes desc, m.memorialApplicationId desc")
  Page<MemorialApplication> findByStateWithOffsetPopular(@Param("state") MemorialApplicationState state, Pageable pageable);

}

