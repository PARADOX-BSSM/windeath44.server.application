package com.example.memorial_application.domain.service;

import com.example.avro.CharacterAvroSchema;
import com.example.avro.MemorialApplicationAvroSchema;
import com.example.avro.MemorialAvroSchema;
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

  public void apply(String userId, Long characterId, String content) {
    MemorialApplication memorialApplication = memorialApplicationMapper.toMemorialApplication(userId, characterId, content);
    // 만약 사용자가 해당 캐릭터에 대한 추모관을 이미 신청한 경우, 실패
    // 중복 신청은 안됨
    if (memorialApplicationRepository.existsByUserIdAndCharacterId(userId, characterId))
      throw AlreadyMemorialApplicationException.getInstance();

    // 만약 해당 캐릭터가 이미 추모중이라면 apply 실패
    grpcClient.validateNotAlreadyMemorialized(characterId);
    memorialApplicationRepository.save(memorialApplication);
  }


  public void approve(Long memorialApplicationId, String userId) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    // kafka로 오케스트레이션 서버에 memorial application approve 요청 with memorialApplicationId
    MemorialApplicationAvroSchema memorialApplicationAvroSchema = memorialApplicationMapper.toMemorialApplicationAvroSchema(memorialApplication, userId);
    kafkaProducer.send("memorial-application-", memorialApplicationAvroSchema);
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
