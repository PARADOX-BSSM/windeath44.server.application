package com.example.memorial_application.domain.service;

import com.example.avro.CharacterAvroSchema;
import com.example.avro.MemorialApplicationAvroSchema;
import com.example.avro.MemorialAvroSchema;
import com.example.memorial_application.domain.dto.request.MemorialApplicationRequest;
import com.example.memorial_application.domain.exception.AlreadyMemorialApplicationException;
import com.example.memorial_application.domain.exception.NotFoundMemorialApplicationException;
import com.example.memorial_application.domain.mapper.MemorialApplicationMapper;
import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.repository.MemorialApplicationRepository;
import com.example.memorial_application.domain.service.gRPC.GrpcClientService;
import com.example.memorial_application.global.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemorialApplicationApproveServiceTest {

    @Mock
    private MemorialApplicationRepository memorialApplicationRepository;

    @Mock
    private MemorialApplicationMapper memorialApplicationMapper;

    @Mock
    private MemorialApplicationFinder finder;

    @Mock
    private GrpcClientService grpcClient;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private MemorialApplicationCommandService memorialApplicationApproveService;

    private String userId;
    private Long characterId;
    private String content;
    private Long memorialApplicationId;
    private MemorialApplication memorialApplication;
    private MemorialApplicationAvroSchema memorialApplicationAvroSchema;
    private CharacterAvroSchema characterAvroSchema;
    private MemorialAvroSchema memorialAvroSchema;
    private MemorialApplicationRequest memorialApplicationRequest;

    @BeforeEach
    void setUp() {
        userId = "testUser";
        characterId = 1L;
        content = "Test content";
        memorialApplicationId = 1L;
        memorialApplication = mock(MemorialApplication.class);
        memorialApplicationAvroSchema = mock(MemorialApplicationAvroSchema.class);
        characterAvroSchema = mock(CharacterAvroSchema.class);
        memorialAvroSchema = mock(MemorialAvroSchema.class);
        memorialApplicationRequest = new MemorialApplicationRequest(1L, "example");

        when(memorialApplication.getMemorialApplicationId()).thenReturn(memorialApplicationId);
        when(memorialApplication.getCharacterId()).thenReturn(characterId);
    }

    @Test
    @DisplayName("Apply should save memorial application when user has not applied for the character")
    void apply_WhenUserHasNotAppliedForCharacter_ShouldSaveMemorialApplication() {
        // Arrange
        when(memorialApplicationMapper.toMemorialApplication(userId, characterId, content)).thenReturn(memorialApplication);
        when(memorialApplicationRepository.existsByUserIdAndCharacterId(userId, characterId)).thenReturn(false);
        doNothing().when(grpcClient).validateNotAlreadyMemorialized(characterId);

        // Act
        memorialApplicationApproveService.apply(userId, memorialApplicationRequest);

        // Assert
        verify(memorialApplicationRepository).save(memorialApplication);
    }

    @Test
    @DisplayName("Apply should throw exception when user has already applied for the character")
    void apply_WhenUserHasAlreadyAppliedForCharacter_ShouldThrowException() {
        // Arrange
        when(memorialApplicationMapper.toMemorialApplication(userId, characterId, content)).thenReturn(memorialApplication);
        when(memorialApplicationRepository.existsByUserIdAndCharacterId(userId, characterId)).thenReturn(true);

        // Act & Assert
        assertThrows(AlreadyMemorialApplicationException.class, () -> 
            memorialApplicationApproveService.apply(userId, memorialApplicationRequest)
        );
        verify(memorialApplicationRepository, never()).save(any(MemorialApplication.class));
    }

    @Test
    @DisplayName("Approve by ID should send Kafka message")
    void approve_ById_ShouldSendKafkaMessage() {
        // Arrange
        when(finder.findMemorialApplicationById(memorialApplicationId)).thenReturn(memorialApplication);
        when(memorialApplicationMapper.toMemorialApplicationAvroSchema(memorialApplication, userId)).thenReturn(memorialApplicationAvroSchema);

        // Act
        memorialApplicationApproveService.approve(memorialApplicationId, userId);

        // Assert
        verify(kafkaProducer).send("memorial-application-", memorialApplicationAvroSchema);
    }

    @Test
    @DisplayName("Reject should update memorial application state to rejected")
    void reject_ShouldUpdateStateToRejected() {
        // Arrange
        when(finder.findMemorialApplicationById(memorialApplicationId)).thenReturn(memorialApplication);

        // Act
        memorialApplicationApproveService.reject(memorialApplicationId);

        // Assert
        verify(memorialApplication).reject();
    }

//    @Test
//    @DisplayName("Approve by CharacterAvroSchema should update state and send Kafka message")
//    void approve_ByCharacterAvroSchema_ShouldUpdateStateAndSendKafkaMessage() {
//        // Arrange
//        when(characterAvroSchema.getApplicantId()).thenReturn(userId);
//        when(characterAvroSchema.getCharacterId()).thenReturn(characterId);
//        when(memorialApplicationRepository.findByUserIdAndCharacterId(userId, characterId)).thenReturn(Optional.of(memorialApplication));
//        when(memorialApplicationMapper.toMemorialApplicationAvroSchema(memorialApplication, userId)).thenReturn(memorialApplicationAvroSchema);
//
//        // Act
//        memorialApplicationApproveService.approve(characterAvroSchema);
//
//        // Assert
//        verify(memorialApplication).approve();
//        verify(memorialApplicationRepository).updateStateToRejectedByCharacterId(memorialApplicationId, characterId);
//        verify(kafkaProducer).send("memorial-creation-orchestration-complete", memorialApplicationAvroSchema);
//    }

    @Test
    @DisplayName("Cancel should update state and send Kafka message")
    void cancel_ShouldUpdateStateAndSendKafkaMessage() {
        // Arrange
        when(memorialAvroSchema.getWriterId()).thenReturn(userId);
        when(memorialAvroSchema.getCharacterId()).thenReturn(characterId);
        when(memorialApplicationRepository.findByUserIdAndCharacterId(userId, characterId)).thenReturn(Optional.of(memorialApplication));
        when(memorialApplicationMapper.toMemorialApplicationAvroSchema(memorialApplication, userId)).thenReturn(memorialApplicationAvroSchema);

        // Act
        memorialApplicationApproveService.cancel(memorialAvroSchema);

        // Assert
        verify(memorialApplication).cancel();
        verify(kafkaProducer).send("memorial-application-cancel-response", memorialApplicationAvroSchema);
    }

    @Test
    @DisplayName("FindApplicationByUserIdAndCharacterId should throw exception when application not found")
    void findApplicationByUserIdAndCharacterId_WhenApplicationNotFound_ShouldThrowException() {
        // Arrange
        when(memorialApplicationRepository.findByUserIdAndCharacterId(userId, characterId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundMemorialApplicationException.class, () -> {
            memorialApplicationApproveService.cancel(memorialAvroSchema);
        });
    }
}