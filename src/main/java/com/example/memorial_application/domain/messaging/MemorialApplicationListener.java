package com.example.memorial_application.domain.messaging;

import com.example.avro.CharacterAvroSchema;
import com.example.avro.MemorialAvroSchema;
import com.example.memorial_application.domain.service.MemorialApplicationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemorialApplicationListener {
  private final MemorialApplicationCommandService memorialApplicationApproveService;

  @KafkaListener(topics = "memorial-application-approved-response", groupId = "memorial")
  public void listenApproved(CharacterAvroSchema message) {
    memorialApplicationApproveService.approve(message);
  }

  @KafkaListener(topics = "memorial-application-cancel-request", groupId = "memorial")
  public void listenCancel(MemorialAvroSchema message) {
    memorialApplicationApproveService.cancel(message);
  }
}
