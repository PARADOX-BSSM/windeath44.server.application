package com.example.memorial_application.domain.service;

import com.example.memorial_application.domain.dto.response.MemorialApplicationListResponse;
import com.example.memorial_application.domain.dto.response.MemorialApplicationResponse;
import com.example.memorial_application.domain.exception.NotFoundMemorialApplicationException;
import com.example.memorial_application.domain.mapper.MemorialApplicationLikesMapper;
import com.example.memorial_application.domain.mapper.MemorialApplicationMapper;
import com.example.memorial_application.domain.model.MemorialApplication;
import com.example.memorial_application.domain.model.MemorialApplicationLikesId;
import com.example.memorial_application.domain.repository.MemorialApplicationLikesRepository;
import com.example.memorial_application.domain.repository.MemorialApplicationRepository;
import com.example.memorial_application.global.mapper.dto.CursorPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemorialApplicationQueryServiceTest {

    @Mock
    private MemorialApplicationRepository memorialApplicationRepository;

    @Mock
    private MemorialApplicationMapper memorialApplicationMapper;

    @Mock
    private MemorialApplicationFinder finder;

    @Mock
    private MemorialApplicationLikesRepository memorialApplicationLikesRepository;

    @Mock
    private MemorialApplicationLikesMapper memorialApplicationLikesMapper;

    @InjectMocks
    private MemorialApplicationQueryService memorialApplicationQueryService;

    private String userId;
    private Long memorialApplicationId;
    private Long characterId;
    private MemorialApplication memorialApplication;
    private MemorialApplicationLikesId memorialApplicationLikesId;
    private MemorialApplicationResponse memorialApplicationResponse;
    private MemorialApplicationListResponse memorialApplicationListResponse1;
    private MemorialApplicationListResponse memorialApplicationListResponse2;
    private List<MemorialApplication> memorialApplicationList;
    private List<MemorialApplicationListResponse> memorialApplicationListResponseList;

    @BeforeEach
    void setUp() {
        userId = "testUser";
        memorialApplicationId = 1L;
        characterId = 1L;
        memorialApplication = mock(MemorialApplication.class);
        memorialApplicationLikesId = mock(MemorialApplicationLikesId.class);
        memorialApplicationResponse = mock(MemorialApplicationResponse.class);
        memorialApplicationListResponse1 = mock(MemorialApplicationListResponse.class);
        memorialApplicationListResponse2 = mock(MemorialApplicationListResponse.class);

        when(memorialApplication.getMemorialApplicationId()).thenReturn(memorialApplicationId);

        memorialApplicationList = Arrays.asList(memorialApplication, memorialApplication);
        memorialApplicationListResponseList = Arrays.asList(memorialApplicationListResponse1, memorialApplicationListResponse2);
    }

    @Test
    @DisplayName("FindAll should return list of memorial applications")
    void findAll_ShouldReturnListOfMemorialApplications() {
        // Arrange
        when(memorialApplicationRepository.findAllSortByLikes()).thenReturn(memorialApplicationList);
        when(memorialApplicationMapper.toMemorialApplicationListResponse(any(MemorialApplication.class)))
                .thenReturn(memorialApplicationListResponse1, memorialApplicationListResponse2);

        // Act
        List<MemorialApplicationListResponse> result = memorialApplicationQueryService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(memorialApplicationRepository).findAllSortByLikes();
        verify(memorialApplicationMapper, times(2)).toMemorialApplicationListResponse(any(MemorialApplication.class));
    }

    @Test
    @DisplayName("FindById should return memorial application response")
    void findById_ShouldReturnMemorialApplicationResponse() {
        // Arrange
        when(finder.findMemorialApplicationById(memorialApplicationId)).thenReturn(memorialApplication);
        when(memorialApplicationLikesMapper.toMemorialApplicationLikeId(memorialApplicationId, userId))
                .thenReturn(memorialApplicationLikesId);
        when(memorialApplicationLikesRepository.existsById(memorialApplicationLikesId)).thenReturn(true);
        when(memorialApplicationMapper.toMemorialApplicationResponse(memorialApplication, true))
                .thenReturn(memorialApplicationResponse);

        // Act
        MemorialApplicationResponse result = memorialApplicationQueryService.findById(memorialApplicationId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(memorialApplicationResponse, result);
        verify(finder).findMemorialApplicationById(memorialApplicationId);
        verify(memorialApplicationLikesMapper).toMemorialApplicationLikeId(memorialApplicationId, userId);
        verify(memorialApplicationLikesRepository).existsById(memorialApplicationLikesId);
        verify(memorialApplicationMapper).toMemorialApplicationResponse(memorialApplication, true);
    }

    @Test
    @DisplayName("DidUserLike should return true when user liked the memorial application")
    void didUserLike_WhenUserLikedMemorialApplication_ShouldReturnTrue() {
        // Arrange
        when(memorialApplicationLikesMapper.toMemorialApplicationLikeId(memorialApplicationId, userId))
                .thenReturn(memorialApplicationLikesId);
        when(memorialApplicationLikesRepository.existsById(memorialApplicationLikesId)).thenReturn(true);
        when(finder.findMemorialApplicationById(memorialApplicationId)).thenReturn(memorialApplication);
        when(memorialApplicationMapper.toMemorialApplicationResponse(memorialApplication, true))
                .thenReturn(memorialApplicationResponse);

        // Act
        MemorialApplicationResponse result = memorialApplicationQueryService.findById(memorialApplicationId, userId);

        // Assert
        assertNotNull(result);
        verify(memorialApplicationLikesRepository).existsById(memorialApplicationLikesId);
        verify(memorialApplicationMapper).toMemorialApplicationResponse(memorialApplication, true);
    }

    @Test
    @DisplayName("FindByCursor should return cursor page of memorial applications")
    void findByCursor_ShouldReturnCursorPageOfMemorialApplications() {
        // Arrange
        Long cursorId = 10L;
        int size = 2;
        Pageable pageable = PageRequest.of(0, size + 1);
        Slice<MemorialApplication> slice = new SliceImpl<>(memorialApplicationList, pageable, true);

        when(memorialApplicationRepository.findPageableByCursor(cursorId, pageable)).thenReturn(slice);
        when(memorialApplicationMapper.toMemorialApplicationPageListResponse(slice))
                .thenReturn(memorialApplicationListResponseList);

        // Act
        CursorPage<MemorialApplicationListResponse> result = memorialApplicationQueryService.findByCursor(cursorId, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.hasNext());
        assertEquals(2, result.values().size());
        verify(memorialApplicationRepository).findPageableByCursor(cursorId, pageable);
        verify(memorialApplicationMapper).toMemorialApplicationPageListResponse(slice);
    }

    @Test
    @DisplayName("FindByCursor with null cursorId should return first page")
    void findByCursor_WithNullCursorId_ShouldReturnFirstPage() {
        // Arrange
        Long cursorId = null;
        int size = 2;
        Pageable pageable = PageRequest.of(0, size + 1);
        Slice<MemorialApplication> slice = new SliceImpl<>(memorialApplicationList, pageable, false);

        when(memorialApplicationRepository.findPageable(pageable)).thenReturn(slice);
        when(memorialApplicationMapper.toMemorialApplicationPageListResponse(slice))
                .thenReturn(memorialApplicationListResponseList);

        // Act
        CursorPage<MemorialApplicationListResponse> result = memorialApplicationQueryService.findByCursor(cursorId, size);

        // Assert
        assertNotNull(result);
        assertFalse(result.hasNext());
        assertEquals(2, result.values().size());
        verify(memorialApplicationRepository).findPageable(pageable);
        verify(memorialApplicationMapper).toMemorialApplicationPageListResponse(slice);
    }

    @Test
    @DisplayName("FindByCharacterId should return memorial application response")
    void findByCharacterId_ShouldReturnMemorialApplicationResponse() {
        // Arrange
        when(memorialApplicationRepository.findByCharacterId(characterId)).thenReturn(Optional.of(memorialApplication));
        when(memorialApplicationLikesMapper.toMemorialApplicationLikeId(memorialApplicationId, userId))
                .thenReturn(memorialApplicationLikesId);
        when(memorialApplicationLikesRepository.existsById(memorialApplicationLikesId)).thenReturn(true);
        when(memorialApplicationMapper.toMemorialApplicationResponse(memorialApplication, true))
                .thenReturn(memorialApplicationResponse);

        // Act
        MemorialApplicationResponse result = memorialApplicationQueryService.findByCharacterId(characterId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(memorialApplicationResponse, result);
        verify(memorialApplicationRepository).findByCharacterId(characterId);
        verify(memorialApplicationLikesMapper).toMemorialApplicationLikeId(memorialApplicationId, userId);
        verify(memorialApplicationLikesRepository).existsById(memorialApplicationLikesId);
        verify(memorialApplicationMapper).toMemorialApplicationResponse(memorialApplication, true);
    }

    @Test
    @DisplayName("FindByCharacterId should throw exception when memorial application not found")
    void findByCharacterId_WhenMemorialApplicationNotFound_ShouldThrowException() {
        // Arrange
        when(memorialApplicationRepository.findByCharacterId(characterId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundMemorialApplicationException.class, () -> 
            memorialApplicationQueryService.findByCharacterId(characterId, userId)
        );
        verify(memorialApplicationRepository).findByCharacterId(characterId);
        verify(memorialApplicationMapper, never()).toMemorialApplicationResponse(any(), anyBoolean());
    }

    @Test
    @DisplayName("FindByCharacterId with null userId should not check likes")
    void findByCharacterId_WithNullUserId_ShouldNotCheckLikes() {
        // Arrange
        String nullUserId = null;
        when(memorialApplicationRepository.findByCharacterId(characterId)).thenReturn(Optional.of(memorialApplication));
        when(memorialApplicationMapper.toMemorialApplicationResponse(memorialApplication, false))
                .thenReturn(memorialApplicationResponse);

        // Act
        MemorialApplicationResponse result = memorialApplicationQueryService.findByCharacterId(characterId, nullUserId);

        // Assert
        assertNotNull(result);
        assertEquals(memorialApplicationResponse, result);
        verify(memorialApplicationRepository).findByCharacterId(characterId);
        verify(memorialApplicationLikesMapper, never()).toMemorialApplicationLikeId(anyLong(), anyString());
        verify(memorialApplicationLikesRepository, never()).existsById(any());
        verify(memorialApplicationMapper).toMemorialApplicationResponse(memorialApplication, false);
    }
}
