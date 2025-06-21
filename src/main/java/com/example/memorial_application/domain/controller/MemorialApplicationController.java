package com.example.memorial_application.domain.controller;

import com.example.memorial_application.domain.dto.request.MemorialApplicationUpadateRequest;
import com.example.memorial_application.global.mapper.ResponseDtoMapper;
import com.example.memorial_application.domain.dto.request.MemorialApplicationRequest;
import com.example.memorial_application.domain.dto.response.MemorialApplicationResponse;
import com.example.memorial_application.global.mapper.dto.CursorPage;
import com.example.memorial_application.global.mapper.dto.ResponseDto;
import com.example.memorial_application.domain.service.MemorialApplicationCommandService;
import com.example.memorial_application.domain.service.MemorialApplicationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.memorial_application.domain.dto.response.MemorialApplicationListResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/memorials/application")
public class MemorialApplicationController {
  private final MemorialApplicationQueryService memorialApplicationQueryService;
  private final MemorialApplicationCommandService memorialApplicationCommandService;
  private final ResponseDtoMapper responseDtoMapper;

  @PostMapping("/apply")
  public ResponseEntity<ResponseDto<Void>> apply(@RequestHeader("user-id") String userId, @RequestBody MemorialApplicationRequest request) {
    Long characterId = request.characterId();
    String content = request.content();
    memorialApplicationCommandService.apply(userId, characterId, content);
    ResponseDto<Void> responseDto = responseDtoMapper.toResponseDto("apply memorial application", null);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(responseDto);
  }

  @DeleteMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> delete(@PathVariable("memorial-application-id") Long memorialApplicationId) {
    memorialApplicationCommandService.delete(memorialApplicationId);
    ResponseDto<Void> responseDto = responseDtoMapper.toResponseDto("delete memorial application", null);
    return ResponseEntity.ok(responseDto);
  }

  @PatchMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> update(@PathVariable("memorial-application-id") Long memorialApplicationId, @RequestBody MemorialApplicationUpadateRequest memorialApplicationUpadteRequest) {
    memorialApplicationCommandService.update(memorialApplicationId, memorialApplicationUpadteRequest);
    ResponseDto<Void> responseDto = responseDtoMapper.toResponseDto("update memorial application", null);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/my")
  public ResponseEntity<ResponseDto<CursorPage<MemorialApplicationListResponse>>> findByUserId(@RequestHeader("user-id") String userId, @RequestParam(value = "cursor-id", required = false) Long cursorId, @RequestParam("size") int size) {
    CursorPage<MemorialApplicationListResponse> memorialApplicationResponse = memorialApplicationQueryService.findMyApplicationByCursor(userId, cursorId, size);
    ResponseDto<CursorPage<MemorialApplicationListResponse>> responseDto = responseDtoMapper.toResponseDto("find my memorial application", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);

  }




  @GetMapping
  public ResponseEntity<ResponseDto<CursorPage<MemorialApplicationListResponse>>> findByCursor(@RequestParam(value = "cursor-id", required = false) Long cursorId, @RequestParam("size") int size) {
    CursorPage<MemorialApplicationListResponse> memorialApplicationResponse = memorialApplicationQueryService.findByCursor(cursorId, size);
    ResponseDto<CursorPage<MemorialApplicationListResponse>> responseDto = responseDtoMapper.toResponseDto("find memorials application with cursor", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/search/character-id")
  public ResponseEntity<ResponseDto<MemorialApplicationResponse>> findByCharacterId(@RequestHeader(value = "user-id", required = false) String userId, @RequestParam("character-id") Long characterId) {
    MemorialApplicationResponse memorialApplicationResponse = memorialApplicationQueryService.findByCharacterId(characterId, userId);
    ResponseDto<MemorialApplicationResponse> responseDto = responseDtoMapper.toResponseDto("find memorial application with characterId", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }


  @GetMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<MemorialApplicationResponse>> findById(@RequestHeader(value = "user-id", required = false) String userId, @PathVariable("memorial-application-id") Long memorialApplicationId) {
    MemorialApplicationResponse memorialApplicationResponse = memorialApplicationQueryService.findById(memorialApplicationId, userId);
    ResponseDto<MemorialApplicationResponse> responseDto = responseDtoMapper.toResponseDto("find memorial application", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @PatchMapping("/approve/{memorial-application-id}/admin")
  public ResponseEntity<ResponseDto<Void>> approve(@PathVariable("memorial-application-id") Long memorialApplicationId, @RequestHeader("user-id") String userId) {
    memorialApplicationCommandService.approve(memorialApplicationId, userId);
    ResponseDto<Void> responseDto = responseDtoMapper.toResponseDto("approve memorial application", null);
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(responseDto);
  }
  
  @PatchMapping("/cancel/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> cancel(@PathVariable("memorial-application-id") Long memorialApplicationId) {
    memorialApplicationCommandService.reject(memorialApplicationId);
    ResponseDto<Void> responseDto = responseDtoMapper.toResponseDto("cancel memorial application", null);
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(responseDto);
  }
}
