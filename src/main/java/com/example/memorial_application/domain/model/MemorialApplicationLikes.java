package com.example.memorial_application.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MemorialApplicationLikes {
  @EmbeddedId
  private MemorialApplicationLikesId id;

  @MapsId("memorialApplicationId")
  @ManyToOne
  @JoinColumn(name = "memorial_application_id")
  private MemorialApplication memorialApplication;

}
