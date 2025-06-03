package com.example.memorial_application.domain.service;

import com.example.memorial_application.domain.domain.MemorialApplication;
import com.example.memorial_application.domain.domain.repository.MemorialApplicationRepository;
import com.example.memorial_application.domain.service.exception.NotFoundMemorialApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemorialApplicationFinder {
  private final MemorialApplicationRepository memorialApplicationRepository;

  public MemorialApplication findMemorialApplicationById(Long memorialApplicationId) {
    return memorialApplicationRepository.findById(memorialApplicationId)
            .orElseThrow(NotFoundMemorialApplicationException::getInstance);
  }

}