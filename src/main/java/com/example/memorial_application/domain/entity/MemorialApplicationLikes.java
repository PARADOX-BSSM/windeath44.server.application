package com.example.memorial_application.domain.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MemorialApplicationLikes {
  @EmbeddedId
  private MemorialApplicationLikesId id;
}
