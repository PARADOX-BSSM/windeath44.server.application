package com.example.memorial_application.domain.presentation;

import com.example.memorial_application.global.mapper.ResponseDtoMapper;
import com.example.memorial_application.domain.presentation.dto.request.MemorialApplicationRequest;
import com.example.memorial_application.domain.presentation.dto.response.MemorialApplicationResponse;
import com.example.memorial_application.global.mapper.dto.CursorPage;
import com.example.memorial_application.global.mapper.dto.ResponseDto;
import com.example.memorial_application.domain.service.MemorialApplicationApproveService;
import com.example.memorial_application.domain.service.MemorialApplicationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.memorial_application.domain.presentation.dto.response.MemorialApplicationListResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/memorials/application")
public class MemorialApplicationController {
  private final MemorialApplicationQueryService memorialApplicationQueryService;
  private final MemorialApplicationApproveService memorialApplicationApproveService;
  private final ResponseDtoMapper responseDtoMapper;

  @PostMapping("/apply")
  public ResponseEntity<ResponseDto<Void>> apply(@RequestHeader("user-id") String userId, @RequestBody MemorialApplicationRequest request) {
    Long characterId = request.characterId();
    String content = request.content();
    memorialApplicationApproveService.apply(userId, characterId, content);
    ResponseDto<Void> responseDto = responseDtoMapper.toResponseDto("apply memorial application", null);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(responseDto);
  }

  @GetMapping("/details")
  public ResponseEntity<ResponseDto<CursorPage<MemorialApplicationListResponse>>> findByCursor(@RequestParam(value = "cursor-id", required = false) Long cursorId, @RequestParam("size") int size) {
    CursorPage<MemorialApplicationListResponse> memorialApplicationResponse = memorialApplicationQueryService.findByCursor(cursorId, size);
    ResponseDto<CursorPage<MemorialApplicationListResponse>> responseDto = responseDtoMapper.toResponseDto("find memorials application with cursor", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/details/{character-id}")
  public ResponseEntity<ResponseDto<MemorialApplicationResponse>> findByCharacterId(@RequestHeader(value = "user-id", required = false) String userId, @PathVariable("character-id") Long characterId) {
    MemorialApplicationResponse memorialApplicationResponse = memorialApplicationQueryService.findByCharacterId(characterId, userId);
    ResponseDto<MemorialApplicationResponse> responseDto = responseDtoMapper.toResponseDto("find memorial application with characterId", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping
  public ResponseEntity<ResponseDto<List<MemorialApplicationListResponse>>> findAll() {
    List<MemorialApplicationListResponse> memorialApplicationResponse = memorialApplicationQueryService.findAll();
    ResponseDto<List<MemorialApplicationListResponse>> responseDto = responseDtoMapper.toResponseDto("find memorials applications", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<MemorialApplicationResponse>> findById(@RequestHeader(value = "user-id", required = false) String userId, @PathVariable("memorial-application-id") Long memorialApplicationId) {
    MemorialApplicationResponse memorialApplicationResponse = memorialApplicationQueryService.findById(memorialApplicationId, userId);
    ResponseDto<MemorialApplicationResponse> responseDto = responseDtoMapper.toResponseDto("find memorial application", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @PatchMapping("/approve/{memorial-application-id}/admin")
  public ResponseEntity<ResponseDto<Void>> approve(@PathVariable("memorial-application-id") Long memorialApplicationId) {
    memorialApplicationApproveService.approve(memorialApplicationId);
    ResponseDto<Void> responseDto = responseDtoMapper.toResponseDto("approve memorial application", null);
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(responseDto);
  }
  
  @PatchMapping("/cancel/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> cancel(@PathVariable("memorial-application-id") Long memorialApplicationId) {
    memorialApplicationApproveService.reject(memorialApplicationId);
    ResponseDto<Void> responseDto = responseDtoMapper.toResponseDto("cancel memorial application", null);
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(responseDto);
  }
}
