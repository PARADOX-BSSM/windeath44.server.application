package com.example.memorial_application.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemorialApplicationLikesId {
  @Column(name="memorial_application_id")
  private Long memorialApplicationId;
  @Column(name="user_id")
  private String userId;

}
