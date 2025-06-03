package com.example.memorial_application.domain.domain.repository;

import com.example.memorial_application.domain.domain.MemorialApplicationLikes;
import com.example.memorial_application.domain.domain.MemorialApplicationLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemorialApplicationLikesRepository extends JpaRepository<MemorialApplicationLikes, MemorialApplicationLikesId> {

}
