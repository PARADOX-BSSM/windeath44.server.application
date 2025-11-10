package com.example.memorial_application.domain.repository;

import com.example.memorial_application.domain.model.MemorialApplicationLikes;
import com.example.memorial_application.domain.model.MemorialApplicationLikesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemorialApplicationLikesRepository extends JpaRepository<MemorialApplicationLikes, MemorialApplicationLikesId> {

}
