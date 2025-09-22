package com.example.memorial_application.domain.service;

import com.example.avro.CharacterAvroSchema;
import com.example.avro.MemorialApplicationAvroSchema;
import com.example.avro.MemorialAvroSchema;
import com.example.memorial_application.domain.dto.request.MemorialApplicationRequest;
import com.example.memorial_application.domain.dto.request.MemorialApplicationUpadateRequest;
import com.example.memorial_application.domain.exception.AlreadyMemorialApplicationException;
import com.example.memorial_application.domain.exception.NotFoundMemorialApplicationException;
import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.mapper.MemorialApplicationMapper;
import com.example.memorial_application.domain.repository.MemorialApplicationRepository;
import com.example.memorial_application.domain.service.gRPC.GrpcClientService;
import com.example.memorial_application.global.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemorialApplicationCommandService {
  private final MemorialApplicationRepository memorialApplicationRepository;
  private final MemorialApplicationMapper memorialApplicationMapper;
  private final MemorialApplicationFinder finder;

  private final GrpcClientService grpcClient;
  private final KafkaProducer kafkaProducer;

  /**
   * 지정한 사용자(userId)가 특정 캐릭터에 대한 추모관 신청을 생성하여 저장한다.
   *
   * 주어진 요청에서 characterId와 content를 추출해 엔티티로 변환한 뒤, 동일 사용자-캐릭터에 대한
   * 기존 신청이 있으면 AlreadyMemorialApplicationException을 던지고,
   * gRPC를 통해 해당 캐릭터가 이미 추모 상태인지 검증한 후 저장한다.
   *
   * @param userId 요청을 생성하는 사용자 식별자
   * @param memorialApplicationRequest 캐릭터 ID와 신청 내용(content)을 포함한 요청 객체
   * @throws AlreadyMemorialApplicationException 동일 사용자와 캐릭터에 대한 신청이 이미 존재할 경우 발생
   */
  public void apply(String userId, MemorialApplicationRequest memorialApplicationRequest) {
    Long characterId = memorialApplicationRequest.characterId();
    String content = memorialApplicationRequest.content();

    MemorialApplication memorialApplication = memorialApplicationMapper.toMemorialApplication(userId, characterId, content);

    // 만약 사용자가 해당 캐릭터에 대한 추모관을 이미 신청한 경우, 실패
    // 중복 신청은 안됨
    boolean existsMemorialApplication = memorialApplicationRepository.existsByUserIdAndCharacterId(userId, characterId);
    if (existsMemorialApplication)
      throw AlreadyMemorialApplicationException.getInstance();

    // 만약 해당 캐릭터가 이미 추모중이라면 apply 실패
    grpcClient.validateNotAlreadyMemorialized(characterId);
    memorialApplicationRepository.save(memorialApplication);
  }


  /**
   * 지정한 장례 신청을 승인 요청하고 승인 정보를 오케스트레이션에 전송한다.
   *
   * <p>주어진 ID로 장례 신청을 조회한 뒤, 대상 캐릭터가 이미 기념 처리되지 않았는지 검증하고,
   * 승인 정보를 Avro 스키마로 변환하여 "memorial-application-approved-request" Kafka 토픽으로 발행한다.
   *
   * @param memorialApplicationId 승인할 장례 신청의 식별자
   * @param userId 승인 요청을 생성한 사용자(요청자)의 식별자 — Avro 메시지에 포함된다
   */
  public void approve(Long memorialApplicationId, String userId) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    // kafka로 오케스트레이션 서버에 memorial application approve 요청 with memorialApplicationId
    grpcClient.validateNotAlreadyMemorialized(memorialApplication.getCharacterId());
    MemorialApplicationAvroSchema memorialApplicationAvroSchema = memorialApplicationMapper.toMemorialApplicationAvroSchema(memorialApplication, userId);
    kafkaProducer.send("memorial-application-approved-request", memorialApplicationAvroSchema);
  }

  private void restMemorialApplicationRejected(MemorialApplication memorialApplication) {
    Long memorialApplicationId = memorialApplication.getMemorialApplicationId();
    Long characterId = memorialApplication.getCharacterId();

    memorialApplicationRepository.updateStateToRejectedByCharacterId(memorialApplicationId, characterId);
  }

  @Transactional
  public void reject(Long memorialApplicationId) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    memorialApplication.reject();
  }

  @Transactional
  public void approve(CharacterAvroSchema message) {
    String applicantId = message.getApplicantId();
    Long characterId = message.getCharacterId();

    MemorialApplication memorialApplication = findApplicationByUserIdAndCharacterId(applicantId, characterId);
    memorialApplication.approve();
    // pending 상태의 같은 캐릭터에 대한 요청들을 rejected 상태로 변환
    restMemorialApplicationRejected(memorialApplication);

    MemorialApplicationAvroSchema memorialApplicationAvroShema = memorialApplicationMapper.toMemorialApplicationAvroSchema(memorialApplication, applicantId);
    kafkaProducer.send("memorial-creation-orchestration-complete", memorialApplicationAvroShema);
  }

  @Transactional
  public void cancel(MemorialAvroSchema message) {
    String applicantId = message.getWriterId();
    Long characterId = message.getCharacterId();

    MemorialApplication memorialApplication = findApplicationByUserIdAndCharacterId(applicantId, characterId);
    memorialApplication.cancel();

    MemorialApplicationAvroSchema memorialApplicationAvroShema = memorialApplicationMapper.toMemorialApplicationAvroSchema(memorialApplication, applicantId);
    kafkaProducer.send("memorial-application-cancel-response", memorialApplicationAvroShema);
  }

  private MemorialApplication findApplicationByUserIdAndCharacterId(String applicantId, Long characterId) {
    return memorialApplicationRepository.findByUserIdAndCharacterId(applicantId, characterId)
            .orElseThrow(NotFoundMemorialApplicationException::getInstance);
  }

  @Transactional
  public void delete(Long memorialApplicationId) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    memorialApplicationRepository.delete(memorialApplication);
  }

  @Transactional
  public void update(Long memorialApplicationId, MemorialApplicationUpadateRequest memorialApplicationUpdateRequest) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    memorialApplication.update(memorialApplicationUpdateRequest.content());
  }
}
