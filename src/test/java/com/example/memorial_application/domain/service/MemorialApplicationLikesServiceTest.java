package com.example.memorial_application.domain.service;

import com.example.memorial_application.domain.exception.AlreadyMemorialApplicationLikesException;
import com.example.memorial_application.domain.exception.NotFoundMemorialApplicationLikesException;
import com.example.memorial_application.domain.mapper.MemorialApplicationLikesMapper;
import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.model.MemorialApplicationLikes;
import com.example.memorial_application.domain.model.MemorialApplicationLikesId;
import com.example.memorial_application.domain.repository.MemorialApplicationLikesRepository;
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
class MemorialApplicationLikesServiceTest {

    @Mock
    private MemorialApplicationLikesRepository memorialApplicationLikesRepository;

    @Mock
    private MemorialApplicationLikesMapper memorialApplicationLikesMapper;

    @Mock
    private MemorialApplicationFinder finder;

    @InjectMocks
    private MemorialApplicationLikesService memorialApplicationLikesService;

    private String userId;
    private Long memorialApplicationId;
    private MemorialApplicationLikesId memorialApplicationLikesId;
    private MemorialApplicationLikes memorialApplicationLikes;
    private MemorialApplication memorialApplication;

    @BeforeEach
    void setUp() {
        userId = "testUser";
        memorialApplicationId = 1L;
        memorialApplicationLikesId = new MemorialApplicationLikesId();
        memorialApplicationLikes = mock(MemorialApplicationLikes.class);
        memorialApplication = mock(MemorialApplication.class);

        when(memorialApplicationLikesMapper.toMemorialApplicationLikeId(memorialApplicationId, userId))
                .thenReturn(memorialApplicationLikesId);
    }

    @Test
    @DisplayName("Like should save memorial application like when user has not liked it")
    void like_WhenUserHasNotLiked_ShouldSaveMemorialApplicationLike() {
        // Arrange
        when(memorialApplicationLikesRepository.existsById(memorialApplicationLikesId)).thenReturn(false);
        when(memorialApplicationLikesMapper.toMemorialApplicationLike(memorialApplicationLikesId)).thenReturn(memorialApplicationLikes);
        when(finder.findMemorialApplicationById(memorialApplicationId)).thenReturn(memorialApplication);

        // Act
        memorialApplicationLikesService.like(userId, memorialApplicationId);

        // Assert
        verify(memorialApplicationLikesRepository).save(memorialApplicationLikes);
        verify(memorialApplication).incrementLikes();
    }

    @Test
    @DisplayName("Like should throw exception when user has already liked the memorial application")
    void like_WhenUserHasAlreadyLiked_ShouldThrowException() {
        // Arrange
        when(memorialApplicationLikesRepository.existsById(memorialApplicationLikesId)).thenReturn(true);

        // Act & Assert
        assertThrows(AlreadyMemorialApplicationLikesException.class, () -> 
            memorialApplicationLikesService.like(userId, memorialApplicationId)
        );
        verify(memorialApplicationLikesRepository, never()).save(any(MemorialApplicationLikes.class));
        verify(memorialApplication, never()).incrementLikes();
    }

    @Test
    @DisplayName("Unlike should delete memorial application like when user has liked it")
    void unlike_WhenUserHasLiked_ShouldDeleteMemorialApplicationLike() {
        // Arrange
        when(memorialApplicationLikesRepository.findById(memorialApplicationLikesId)).thenReturn(Optional.of(memorialApplicationLikes));
        when(finder.findMemorialApplicationById(memorialApplicationId)).thenReturn(memorialApplication);

        // Act
        memorialApplicationLikesService.unlike(userId, memorialApplicationId);

        // Assert
        verify(memorialApplicationLikesRepository).delete(memorialApplicationLikes);
        verify(memorialApplication).decrementLikes();
    }

    @Test
    @DisplayName("Unlike should throw exception when user has not liked the memorial application")
    void unlike_WhenUserHasNotLiked_ShouldThrowException() {
        // Arrange
        when(memorialApplicationLikesRepository.findById(memorialApplicationLikesId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundMemorialApplicationLikesException.class, () -> 
            memorialApplicationLikesService.unlike(userId, memorialApplicationId)
        );
        verify(memorialApplicationLikesRepository, never()).delete(any(MemorialApplicationLikes.class));
        verify(memorialApplication, never()).decrementLikes();
    }

    @Test
    @DisplayName("SaveMemorialApplicationLike should save like and increment likes count")
    void saveMemorialApplicationLike_ShouldSaveLikeAndIncrementLikesCount() {
        // Arrange
        when(memorialApplicationLikesMapper.toMemorialApplicationLike(memorialApplicationLikesId)).thenReturn(memorialApplicationLikes);
        when(finder.findMemorialApplicationById(memorialApplicationId)).thenReturn(memorialApplication);

        // Act
        memorialApplicationLikesService.like(userId, memorialApplicationId);

        // Assert
        verify(memorialApplicationLikesRepository).save(memorialApplicationLikes);
        verify(memorialApplication).incrementLikes();
    }

    @Test
    @DisplayName("DeleteMemorialApplicationLike should delete like and decrement likes count")
    void deleteMemorialApplicationLike_ShouldDeleteLikeAndDecrementLikesCount() {
        // Arrange
        when(memorialApplicationLikesRepository.findById(memorialApplicationLikesId)).thenReturn(Optional.of(memorialApplicationLikes));
        when(finder.findMemorialApplicationById(memorialApplicationId)).thenReturn(memorialApplication);

        // Act
        memorialApplicationLikesService.unlike(userId, memorialApplicationId);

        // Assert
        verify(memorialApplicationLikesRepository).delete(memorialApplicationLikes);
        verify(memorialApplication).decrementLikes();
    }
}