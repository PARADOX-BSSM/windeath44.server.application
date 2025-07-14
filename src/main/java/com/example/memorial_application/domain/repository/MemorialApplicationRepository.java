package com.example.memorial_application.domain.repository;

import com.example.memorial_application.domain.model.MemorialApplication;
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

  @Modifying
  @Query(value = "update MemorialApplication m set m.state = 'REJECTED' where m.characterId = :characterId and m.memorialApplicationId != :memorialApplicationId")
  void updateStateToRejectedByCharacterId(@Param("memorialApplicationId") Long memorialApplicationId, @Param("characterId") Long characterId);

  @Query(value = "select m from MemorialApplication m where m.memorialApplicationId > :cursorId order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findPageableByCursor(@Param("cursorId") Long cursorId, Pageable pageable);

  @Query(value = "select m from MemorialApplication m order by m.memorialApplicationId asc")
  Slice<MemorialApplication> findPageable(Pageable pageable);

  Optional<MemorialApplication> findByUserIdAndCharacterId(String applicantId, Long characterId);

  Boolean existsByUserIdAndCharacterId(String userId, Long characterId);

  @Query("select m from MemorialApplication m where m.userId = :userId order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findMyApplicationPageable(@Param("userId") String userId, Pageable pageable);

  @Query("select m from MemorialApplication m where m.userId = :userId and m.memorialApplicationId < :cursorId order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findMyApplicationPageableByCursorId(@Param("userId") String userId, @Param("cursorId") Long cursorId, Pageable pageable);

  @Query(value = "select m from MemorialApplication m where m.characterId = :characterId order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findByCharacterId(Long characterId, Pageable pageable);

  @Query(value = "select m from MemorialApplication m where m.characterId = :characterId and m.memorialApplicationId <= :cursorId order by m.memorialApplicationId desc")
  Slice<MemorialApplication> findByCharacterIdAndCursorId(Long characterId, Pageable pageable, Long cursorId);
}

