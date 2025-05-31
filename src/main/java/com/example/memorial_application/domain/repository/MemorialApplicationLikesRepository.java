package com.example.memorial_application.domain.repository;

import com.example.memorial_application.domain.entity.MemorialApplicationLikes;
import com.example.memorial_application.domain.entity.MemorialApplicationLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemorialApplicationLikesRepository extends JpaRepository<MemorialApplicationLikes, MemorialApplicationLikesId> {

}
