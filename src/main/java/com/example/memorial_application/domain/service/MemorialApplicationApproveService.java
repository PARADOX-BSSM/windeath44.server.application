package com.example.memorial_application.domain.service;

import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.mapper.MemorialApplicationMapper;
import com.example.memorial_application.domain.repository.MemorialApplicationRepository;
import com.example.memorial_application.domain.service.gRPC.GrpcClientService;
import com.example.memorial_application.global.utils.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemorialApplicationApproveService {
  private final MemorialApplicationRepository memorialApplicationRepository;
  private final MemorialApplicationMapper memorialApplicationMapper;
  private final MemorialApplicationFinder finder;

  private final GrpcClientService grpcClient;
  private final KafkaProducer kafkaProducer;

  public void apply(String userId, Long characterId, String content) {
    MemorialApplication memorialApplication = memorialApplicationMapper.toMemorialApplication(userId, characterId, content);
    // 만약 해당 캐릭터가 이미 추모중이라면 apply 실패
    grpcClient.validateNotAlreadyMemorialized(characterId);
    memorialApplicationRepository.save(memorialApplication);
  }

  @Transactional
  public void approve(Long memorialApplicationId) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    memorialApplication.approve();
    // kafka로 오케스트레이션 서버에 memorial application approve 요청 with memorialApplicationId
    kafkaProducer.send("approved-memorial-application", memorialApplicationId.toString());

    // 오케스트레이션 서버에서 kafka로 memorial 서버로 생성 요청
    // 오케스트레이션 서버에서 kafka로 anime 서버의 캐릭터 상태를 'NOT_MEMORIALIZING' -> 'MEMORIALIZING'으로 변환
    // pending 상태의 같은 캐릭터에 대한 요청들을 rejected 상태로 변환
    restMemorialApplicationRejected(memorialApplicationId, memorialApplication);
  }

  private void restMemorialApplicationRejected(Long memorialApplicationId, MemorialApplication memorialApplication) {
    Long characterId = memorialApplication.getCharacterId();
    memorialApplicationRepository.updateStateToRejectedByCharacterId(memorialApplicationId, characterId);
  }

  @Transactional
  public void reject(Long memorialApplicationId) {
    MemorialApplication memorialApplication = finder.findMemorialApplicationById(memorialApplicationId);
    memorialApplication.reject();
  }

}
