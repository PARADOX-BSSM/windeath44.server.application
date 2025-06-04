package com.example.memorial_application.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MemorialApplication {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memorialApplicationId;

  private String userId;
  private Long characterId;
  private String content;
  private Long likes;
  @CreatedDate
  private LocalDateTime createdAt;
  @Enumerated(EnumType.STRING)
  private MemorialApplicationState state;
  @Version
  private Integer version;

  @PrePersist
  public void init() {
    this.createdAt = LocalDateTime.now();
    this.likes = 0L;
  }
  public void reject() {
    this.state = MemorialApplicationState.REJECTED;
  }

  public void approve() {
    this.state = MemorialApplicationState.APPROVED;
  }


  public void countLikes(Long likes) {
    this.likes = likes;
  }

  public void incrementLikes() {
    this.likes++;
  }

  public void decrementLikes() {
    this.likes--;
  }
}

