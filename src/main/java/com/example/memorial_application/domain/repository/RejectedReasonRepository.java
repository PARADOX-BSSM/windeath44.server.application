package com.example.memorial_application.domain.repository;

import com.example.memorial_application.domain.model.RejectedReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RejectedReasonRepository extends JpaRepository<RejectedReason, Long> {
}
