package com.example.memorial_application.domain.controller;
import com.example.memorial_application.domain.dto.request.MemorialApplicationUpadateRequest;
import com.example.memorial_application.domain.dto.request.MemorialApplicationRequest;
import com.example.memorial_application.global.dto.CursorPage;
import com.example.memorial_application.global.dto.ResponseDto;
import com.example.memorial_application.domain.service.MemorialApplicationCommandService;
import com.example.memorial_application.domain.service.MemorialApplicationQueryService;
import com.example.memorial_application.global.util.HttpUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.memorial_application.domain.dto.response.MemorialApplicationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/applications")
public class MemorialApplicationController {
  private final MemorialApplicationQueryService memorialApplicationQueryService;
  private final MemorialApplicationCommandService memorialApplicationCommandService;

  @PostMapping("/apply")
  public ResponseEntity<ResponseDto<Void>> apply(@RequestHeader("user-id") String userId, @RequestBody @Valid MemorialApplicationRequest request) {
    memorialApplicationCommandService.apply(userId, request);
    ResponseDto<Void> responseDto = HttpUtil.success("apply memorial application");
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(responseDto);
  }

  @DeleteMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> delete(@PathVariable("memorial-application-id") Long memorialApplicationId) {
    memorialApplicationCommandService.delete(memorialApplicationId);
    ResponseDto<Void> responseDto = HttpUtil.success("delete memorial application");
    return ResponseEntity.ok(responseDto);
  }

  @PatchMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> update(@PathVariable("memorial-application-id") Long memorialApplicationId, @RequestBody @Valid MemorialApplicationUpadateRequest memorialApplicationUpadteRequest) {
    memorialApplicationCommandService.update(memorialApplicationId, memorialApplicationUpadteRequest);
    ResponseDto<Void> responseDto = HttpUtil.success("update memorial application");
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/my")
  public ResponseEntity<ResponseDto<CursorPage<MemorialApplicationResponse>>> findByUserId(
          @RequestHeader("user-id") String userId,
          @RequestParam(value = "cursorId", required = false) Long cursorId,
          @RequestParam("size") int size
  ) {
    CursorPage<MemorialApplicationResponse> memorialApplicationResponse = memorialApplicationQueryService.findMyApplicationByCursor(userId, cursorId, size);
    ResponseDto<CursorPage<MemorialApplicationResponse>> responseDto = HttpUtil.success("find my memorial application", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping
  public ResponseEntity<ResponseDto<CursorPage<MemorialApplicationResponse>>> findByCursor(
          @RequestHeader("user-id") String userId,
          @RequestParam(value = "cursorId", required = false) Long cursorId,
          @RequestParam("size") int size,
          @RequestParam(value="memorizingCode", required = false) Integer memorizingCode
  ) {
    CursorPage<MemorialApplicationResponse> memorialApplicationResponse = memorialApplicationQueryService.findByCursor(cursorId, size, memorizingCode, userId);
    ResponseDto<CursorPage<MemorialApplicationResponse>> responseDto = HttpUtil.success("find memorials application with cursor", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/search")
  public ResponseEntity<ResponseDto<CursorPage<MemorialApplicationResponse>>> findByCharacterId(
          @RequestHeader("user-id") String userId,
          @RequestParam("characterId") Long characterId,
          @RequestParam(value = "cursorId", required = false) Long cursorId,
          @RequestParam("size") int size
  ) {
    CursorPage<MemorialApplicationResponse> memorialApplicationResponse = memorialApplicationQueryService.findByCharacterId(characterId, cursorId, size, userId);
    ResponseDto<CursorPage<MemorialApplicationResponse>> responseDto = HttpUtil.success("find memorial application with characterId", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/{memorial-application-id}")
  public ResponseEntity<ResponseDto<MemorialApplicationResponse>> findById
          (@RequestHeader(value = "user-id", required = false) String userId,
           @PathVariable("memorial-application-id") Long memorialApplicationId) {
    MemorialApplicationResponse memorialApplicationResponse = memorialApplicationQueryService.findById(memorialApplicationId, userId);
    ResponseDto<MemorialApplicationResponse> responseDto = HttpUtil.success("find memorial application", memorialApplicationResponse);
    return ResponseEntity.ok(responseDto);
  }

  @PatchMapping("/approve/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> approve(@PathVariable("memorial-application-id") Long memorialApplicationId, @RequestHeader("user-id") String userId) {
    memorialApplicationCommandService.approve(memorialApplicationId, userId);
    ResponseDto<Void> responseDto = HttpUtil.success("approve memorial application");
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(responseDto);
  }

  @PatchMapping("/cancel/{memorial-application-id}")
  public ResponseEntity<ResponseDto<Void>> cancel(@PathVariable("memorial-application-id") Long memorialApplicationId) {
    memorialApplicationCommandService.reject(memorialApplicationId);
    ResponseDto<Void> responseDto = HttpUtil.success("cancel memorial application");
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(responseDto);
  }
}
