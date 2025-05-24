package com.springboot.api.service;

import com.springboot.api.counselcard.repository.CounselCardRepository;
import com.springboot.api.counselcard.service.CounselCardService;
import com.springboot.api.counselcard.model.CounselCard;
import com.springboot.api.counselcard.model.CounselCardRecordType;
import com.springboot.api.counselcard.model.CounselCardStatus;
import com.springboot.api.counselcard.request.CounselCardUpdateRequest;
import com.springboot.api.counselcard.response.CounselCardBaseInformationResponse;
import com.springboot.api.counselcard.response.CounselCardHealthInformationResponse;
import com.springboot.api.counselcard.response.CounselCardIndependentLifeInformationResponse;
import com.springboot.api.counselcard.response.CounselCardLivingInformationResponse;
import com.springboot.api.counselcard.response.CounselCardResponse;
import com.springboot.api.counselcard.response.PreviousRecordResponse;
import com.springboot.api.counselee.model.Counselee;
import com.springboot.api.counselsession.model.CounselSession;
import com.springboot.api.exception.NoContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselCardServiceTest {

    @Mock
    private CounselCardRepository counselCardRepository;

    @InjectMocks
    private CounselCardService counselCardService;

    private CounselCard createMockCounselCard(Long id, CounselSession counselSession) {
        return CounselCard.builder()
                .id(id)
                .counselSession(counselSession)
                .build();
    }

    private CounselSession createMockCounselSession(Long id, Counselee counselee) {
        return CounselSession.builder()
                .id(id)
                .counselee(counselee)
                .build();
    }

    private Counselee createMockCounselee(Long id) {
        return Counselee.builder()
                .id(id)
                .build();
    }

    @Test
    @DisplayName("상담카드 조회 - 성공")
    void selectCounselCard_CardExists_ReturnsCounselCard() {
        // given
        Long counselSessionId = 1L;
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = createMockCounselCard(1L, counselSession);
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.of(counselCard));

        // when
        CounselCardResponse response = counselCardService.selectCounselCard(counselSessionId);

        // then
        assertNotNull(response);
        assertEquals(counselCard.getId(), response.getCounselCardId());
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 조회 - 실패 (카드가 존재하지 않음)")
    void selectCounselCard_CardDoesNotExist_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.selectCounselCard(counselSessionId);
        });
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 기본 정보 조회 - 성공")
    void selectCounselCardBaseInformation_CardExists_ReturnsBaseInformation() {
        // given
        Long counselSessionId = 1L;
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = createMockCounselCard(1L, counselSession);
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.of(counselCard));

        // when
        CounselCardBaseInformationResponse response = counselCardService.selectCounselCardBaseInformation(counselSessionId);

        // then
        assertNotNull(response);
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 기본 정보 조회 - 실패 (카드가 존재하지 않음)")
    void selectCounselCardBaseInformation_CardDoesNotExist_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.selectCounselCardBaseInformation(counselSessionId);
        });
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 건강 정보 조회 - 성공")
    void selectCounselCardHealthInformation_CardExists_ReturnsHealthInformation() {
        // given
        Long counselSessionId = 1L;
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = createMockCounselCard(1L, counselSession);
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.of(counselCard));

        // when
        CounselCardHealthInformationResponse response = counselCardService.selectCounselCardHealthInformation(counselSessionId);

        // then
        assertNotNull(response);
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 건강 정보 조회 - 실패 (카드가 존재하지 않음)")
    void selectCounselCardHealthInformation_CardDoesNotExist_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.selectCounselCardHealthInformation(counselSessionId);
        });
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 독립생활 정보 조회 - 성공")
    void selectCounselCardIndependentLifeInformation_CardExists_ReturnsIndependentLifeInformation() {
        // given
        Long counselSessionId = 1L;
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = createMockCounselCard(1L, counselSession);
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.of(counselCard));

        // when
        CounselCardIndependentLifeInformationResponse response = counselCardService.selectCounselCardIndependentLifeInformation(counselSessionId);

        // then
        assertNotNull(response);
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 독립생활 정보 조회 - 실패 (카드가 존재하지 않음)")
    void selectCounselCardIndependentLifeInformation_CardDoesNotExist_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.selectCounselCardIndependentLifeInformation(counselSessionId);
        });
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 생활환경 정보 조회 - 성공")
    void selectCounselCardLivingInformation_CardExists_ReturnsLivingInformation() {
        // given
        Long counselSessionId = 1L;
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = createMockCounselCard(1L, counselSession);
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.of(counselCard));

        // when
        CounselCardLivingInformationResponse response = counselCardService.selectCounselCardLivingInformation(counselSessionId);

        // then
        assertNotNull(response);
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 생활환경 정보 조회 - 실패 (카드가 존재하지 않음)")
    void selectCounselCardLivingInformation_CardDoesNotExist_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.selectCounselCardLivingInformation(counselSessionId);
        });
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 상태 변경 - IN_PROGRESS (이전 카드 없음)")
    void updateCounselCardStatus_ToInProgress_NoPreviousCard_Success() {
        // given
        Long counselSessionId = 1L;
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = spy(createMockCounselCard(1L, counselSession)); // Use spy to verify method calls on the instance

        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.of(counselCard));
        when(counselCardRepository.findLastRecordedCounselCard(anyLong(), anyLong())).thenReturn(Optional.empty()); // No previous card

        // when
        counselCardService.updateCounselCardStatus(counselSessionId, CounselCardStatus.IN_PROGRESS);

        // then
        verify(counselCard).updateStatusToInProgress(Optional.empty());
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
        verify(counselCardRepository).findLastRecordedCounselCard(eq(counselee.getId()), eq(counselCard.getId()));
    }

    @Test
    @DisplayName("상담카드 상태 변경 - IN_PROGRESS (이전 카드 있음)")
    void updateCounselCardStatus_ToInProgress_WithPreviousCard_Success() {
        // given
        Long currentCounselSessionId = 1L;
        Long previousCounselSessionId = 2L;
        Counselee counselee = createMockCounselee(1L);

        CounselSession currentCounselSession = createMockCounselSession(currentCounselSessionId, counselee);
        CounselCard currentCounselCard = spy(createMockCounselCard(1L, currentCounselSession));


        CounselSession previousCounselSession = createMockCounselSession(previousCounselSessionId, counselee);
        CounselCard previousCounselCard = createMockCounselCard(2L, previousCounselSession);


        when(counselCardRepository.findCounselCardByCounselSessionId(currentCounselSessionId)).thenReturn(Optional.of(currentCounselCard));
        when(counselCardRepository.findLastRecordedCounselCard(anyLong(), anyLong())).thenReturn(Optional.of(previousCounselCard));

        // when
        counselCardService.updateCounselCardStatus(currentCounselSessionId, CounselCardStatus.IN_PROGRESS);

        // then
        verify(currentCounselCard).updateStatusToInProgress(Optional.of(previousCounselCard));
        verify(counselCardRepository).findCounselCardByCounselSessionId(currentCounselSessionId);
        verify(counselCardRepository).findLastRecordedCounselCard(eq(counselee.getId()), eq(currentCounselCard.getId()));
    }


    @Test
    @DisplayName("상담카드 상태 변경 - COMPLETED")
    void updateCounselCardStatus_ToCompleted_Success() {
        // given
        Long counselSessionId = 1L;
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = spy(createMockCounselCard(1L, counselSession)); // Use spy
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.of(counselCard));

        // when
        counselCardService.updateCounselCardStatus(counselSessionId, CounselCardStatus.COMPLETED);

        // then
        verify(counselCard).updateStatusToCompleted();
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 상태 변경 - 실패 (NOT_STARTED)")
    void updateCounselCardStatus_ToNotStarted_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.updateCounselCardStatus(counselSessionId, CounselCardStatus.NOT_STARTED);
        });
    }

    @Test
    @DisplayName("상담카드 상태 변경 - 실패 (카드가 존재하지 않음)")
    void updateCounselCardStatus_CardDoesNotExist_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.updateCounselCardStatus(counselSessionId, CounselCardStatus.IN_PROGRESS);
        });
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 초기화 - 성공")
    void initializeCounselCard_NoExistingCard_Success() {
        // given
        Long counselSessionId = 1L;
        CounselSession counselSession = CounselSession.builder().id(counselSessionId).build(); // Mock or build a simple CounselSession
        when(counselCardRepository.existsByCounselSessionId(counselSessionId)).thenReturn(false);
        when(counselCardRepository.save(any(CounselCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        assertDoesNotThrow(() -> counselCardService.initializeCounselCard(counselSession));

        // then
        verify(counselCardRepository).existsByCounselSessionId(counselSessionId);
        verify(counselCardRepository).save(any(CounselCard.class));
    }

    @Test
    @DisplayName("상담카드 초기화 - 실패 (카드가 이미 존재함)")
    void initializeCounselCard_CardAlreadyExists_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        CounselSession counselSession = CounselSession.builder().id(counselSessionId).build();
        when(counselCardRepository.existsByCounselSessionId(counselSessionId)).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.initializeCounselCard(counselSession);
        });
        verify(counselCardRepository).existsByCounselSessionId(counselSessionId);
        verify(counselCardRepository, never()).save(any(CounselCard.class));
    }

    @Test
    @DisplayName("상담카드 수정 - 성공")
    void updateCounselCard_CardExists_Success() {
        // given
        Long counselSessionId = 1L;
        CounselCardUpdateRequest request = new CounselCardUpdateRequest(); // Populate with test data if needed
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = spy(createMockCounselCard(1L, counselSession)); // Use spy
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.of(counselCard));

        // when
        counselCardService.updateCounselCard(counselSessionId, request);

        // then
        verify(counselCard).update(request);
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 수정 - 실패 (카드가 존재하지 않음)")
    void updateCounselCard_CardDoesNotExist_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        CounselCardUpdateRequest request = new CounselCardUpdateRequest();
        when(counselCardRepository.findCounselCardByCounselSessionId(counselSessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.updateCounselCard(counselSessionId, request);
        });
        verify(counselCardRepository).findCounselCardByCounselSessionId(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 삭제 - 성공")
    void deleteCounselCard_CardExists_Success() {
        // given
        Long counselSessionId = 1L;
        Counselee counselee = createMockCounselee(1L);
        CounselSession counselSession = createMockCounselSession(counselSessionId, counselee);
        CounselCard counselCard = createMockCounselCard(1L, counselSession);
        when(counselCardRepository.findCounselCardWithCounselee(counselSessionId)).thenReturn(Optional.of(counselCard));

        // when
        counselCardService.deleteCounselCard(counselSessionId);

        // then
        verify(counselCardRepository).delete(counselCard);
        verify(counselCardRepository).findCounselCardWithCounselee(counselSessionId);
    }

    @Test
    @DisplayName("상담카드 삭제 - 실패 (카드가 존재하지 않음)")
    void deleteCounselCard_CardDoesNotExist_ThrowsIllegalArgumentException() {
        // given
        Long counselSessionId = 1L;
        when(counselCardRepository.findCounselCardWithCounselee(counselSessionId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            counselCardService.deleteCounselCard(counselSessionId);
        });
        verify(counselCardRepository).findCounselCardWithCounselee(counselSessionId);
        verify(counselCardRepository, never()).delete(any(CounselCard.class));
    }

    @Test
    @DisplayName("이전 기록 조회 - 데이터 있음")
    void selectPreviousRecordsByType_RecordsExist_ReturnsData() {
        // given
        Long counseleeId = 1L;
        Long currentCounselCardId = 10L;
        CounselCardRecordType type = CounselCardRecordType.DRINKING_HISTORY; // Example type
        List<CounselCard> previousCards = List.of(
                CounselCard.builder().drinkingProblem("Previous problem").build() // Simplified for testing
        );
        when(counselCardRepository.findRecordedCardsByPreviousSessions(counseleeId, currentCounselCardId)).thenReturn(previousCards);

        // when
        PreviousRecordResponse response = counselCardService.selectPreviousRecordsByType(counseleeId, currentCounselCardId, type);

        // then
        assertNotNull(response);
        assertFalse(response.getRecords().isEmpty());
        assertEquals("Previous problem", response.getRecords().get(0)); // Adjust based on actual data extraction
        verify(counselCardRepository).findRecordedCardsByPreviousSessions(counseleeId, currentCounselCardId);
    }

    @Test
    @DisplayName("이전 기록 조회 - 데이터 없음")
    void selectPreviousRecordsByType_NoRecordsExist_ThrowsNoContentException() {
        // given
        Long counseleeId = 1L;
        Long currentCounselCardId = 10L;
        CounselCardRecordType type = CounselCardRecordType.DRINKING_HISTORY;
        when(counselCardRepository.findRecordedCardsByPreviousSessions(counseleeId, currentCounselCardId)).thenReturn(Collections.emptyList());

        // when & then
        assertThrows(NoContentException.class, () -> {
            counselCardService.selectPreviousRecordsByType(counseleeId, currentCounselCardId, type);
        });
        verify(counselCardRepository).findRecordedCardsByPreviousSessions(counseleeId, currentCounselCardId);
    }
}
