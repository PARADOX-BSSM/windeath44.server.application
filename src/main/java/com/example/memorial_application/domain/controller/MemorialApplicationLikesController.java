package com.example.memorial_application.domain.controller;

import com.example.memorial_application.global.dto.ResponseDto;
import com.example.memorial_application.domain.service.MemorialApplicationLikesService;
import com.example.memorial_application.global.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/applications/likes")
public class MemorialApplicationLikesController {
  private final MemorialApplicationLikesService memorialApplicationLikesService;

  @PostMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> like(@RequestHeader("user-id") String userId, @PathVariable("memorial-application-id") Long memorialApplicationId) {
    userId = userId.substring(2, userId.length() - 2);
    memorialApplicationLikesService.like(userId, memorialApplicationId);
    ResponseDto<Void> responseDto = HttpUtil.success("like memorial application");
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(responseDto);
  }

  @DeleteMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> unlike(@RequestHeader("user-id") String userId, @PathVariable("memorial-application-id") Long memorialApplicationId) {
    userId = userId.substring(2, userId.length() - 2);
    memorialApplicationLikesService.unlike(userId, memorialApplicationId);
    ResponseDto<Void> responseDto = HttpUtil.success("unlike memorial application");
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(responseDto);
  }

}


