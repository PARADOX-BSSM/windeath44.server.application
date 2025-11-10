package com.example.memorial_application.domain.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class MemorialApplication {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memorialApplicationId;

  private String userId;
  private Long characterId;
  @Column(columnDefinition = "TEXT")
  private String content;
  private Long likes;
  @CreatedDate
  private LocalDateTime createdAt;
  @Enumerated(EnumType.STRING)
  private MemorialApplicationState state;

  @OneToMany(mappedBy = "memorialApplication", orphanRemoval = true, cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<MemorialApplicationLikes> memorialApplicationLikes;

  @OneToOne(mappedBy = "memorialApplication", cascade = CascadeType.ALL)
  @Fetch(FetchMode.JOIN)
  private RejectedReason rejectedReason;

  @PrePersist
  public void init() {
    this.likes = 0L;
  }
  public void reject(RejectedReason rejectedReason) {
    this.rejectedReason = rejectedReason;
    this.state = MemorialApplicationState.REJECTED;
  }

  public void approve() {
    this.state = MemorialApplicationState.APPROVED;
  }

  public void cancel() {
    this.state  = MemorialApplicationState.PENDING;
  }

  public boolean didUserLiked(String viewerId) {
    return this.memorialApplicationLikes.stream()
            .anyMatch(like -> like.didUserLiked(viewerId));
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

  public void update(String content) {
    this.content = content;
  }

  public LocalDate getCreatedAt() {
    int year = this.createdAt.getYear();
    Month month = this.createdAt.getMonth();
    int day = this.createdAt.getDayOfMonth();
    return LocalDate.of(year, month, day);
  }

  public String getReason() {
    return this.rejectedReason.getReason();
  }
}

