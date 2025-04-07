package com.springboot.api.counselcard.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.counselcard.dto.request.UpdateBaseInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateCounselCardReq;
import com.springboot.api.counselcard.dto.request.UpdateHealthInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateIndependentLifeInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateLivingInformationReq;
import com.springboot.api.counselcard.dto.response.CounselCardBaseInformationRes;
import com.springboot.api.counselcard.dto.response.CounselCardHealthInformationRes;
import com.springboot.api.counselcard.dto.response.CounselCardIdRes;
import com.springboot.api.counselcard.dto.response.CounselCardIndependentLifeInformationRes;
import com.springboot.api.counselcard.dto.response.CounselCardLivingInformationRes;
import com.springboot.api.counselcard.dto.response.CounselCardRes;
import com.springboot.api.counselcard.dto.response.TimeRecordedRes;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.api.counselcard.repository.CounselCardRepository;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.CounselCardRecordType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounselCardService {

    private final CounselCardRepository counselCardRepository;

    public CounselCardRes selectCounselCard(String counselSessionId) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardWithCounselee(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        return new CounselCardRes(counselCard);
    }

    public CounselCardBaseInformationRes selectCounselCardBaseInformation(String counselSessionId) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardWithCounselee(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        return new CounselCardBaseInformationRes(counselCard);
    }

    public CounselCardHealthInformationRes selectCounselCardHealthInformation(String counselSessionId) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardWithCounselee(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        return new CounselCardHealthInformationRes(counselCard);
    }

    public CounselCardIndependentLifeInformationRes selectCounselCardIndependentLifeInformation(
        String counselSessionId) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardWithCounselee(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        return new CounselCardIndependentLifeInformationRes(counselCard);
    }

    public CounselCardLivingInformationRes selectCounselCardLivingInformation(String counselSessionId) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardWithCounselee(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        return new CounselCardLivingInformationRes(counselCard);
    }

    @Transactional
    @CacheEvict(value = "sessionList", allEntries = true)
    public CounselCardIdRes updateCounselCardStatus(String counselSessionId, CardRecordStatus status) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardByCounselSessionId(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        switch (status) {
            case NOT_STARTED -> throw new IllegalArgumentException("상담 카드의 상태를 NOT_STARTED로 변경할 수 없습니다");
            case IN_PROGRESS -> {
                retrievePreviousCounselCardAndFill(counselCard);
                counselCard.updateStatusToInProgress();
            }
            case COMPLETED -> counselCard.updateStatusToCompleted();
        }

        return new CounselCardIdRes(counselCard.getId());
    }

    @Transactional
    public void initializeCounselCard(CounselSession counselSession) {
        if (counselCardRepository.existsByCounselSessionId(counselSession.getId())) {
            throw new IllegalArgumentException("해당 상담 세션에 대한 상담 카드가 이미 존재합니다");
        }

        CounselCard counselCard = CounselCard.createFromSession(counselSession);

        counselCardRepository.save(counselCard);
    }

    private void retrievePreviousCounselCardAndFill(CounselCard counselCard) {
        CounselCard lastRecordedCounselCard =
            counselCardRepository.findLastRecordedCounselCard(counselCard.getCounselSession().getCounselee().getId())
                .orElse(null);

        counselCard.importPreviousCardData(lastRecordedCounselCard);
    }

    @Transactional
    public CounselCardIdRes updateCounselCard(String counselSessionId, UpdateCounselCardReq updateCounselCardReq) {
        CounselCard counselCard = counselCardRepository.findCounselCardByCounselSessionId(
                counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        counselCard.update(updateCounselCardReq);

        return new CounselCardIdRes(counselCard.getId());
    }

    @Transactional
    public CounselCardIdRes updateCounselCardBaseInformation(String counselSessionId,
        UpdateBaseInformationReq updateBaseInformationReq) {
        CounselCard counselCard = counselCardRepository.findCounselCardByCounselSessionId(
                counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        counselCard.updateBaseInformation(updateBaseInformationReq);

        return new CounselCardIdRes(counselCard.getId());
    }

    @Transactional
    public CounselCardIdRes updateCounselCardHealthInformation(String counselSessionId,
        UpdateHealthInformationReq updateHealthInformationReq) {
        CounselCard counselCard = counselCardRepository.findCounselCardByCounselSessionId(
                counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        counselCard.updateHealthInformation(updateHealthInformationReq);

        return new CounselCardIdRes(counselCard.getId());
    }

    @Transactional
    public CounselCardIdRes updateCounselCardIndependentLifeInformation(
        String counselSessionId,
        UpdateIndependentLifeInformationReq updateIndependentLifeInformationReq) {
        CounselCard counselCard = counselCardRepository.findCounselCardWithCounselee(
                counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        counselCard.updateIndependentLife(updateIndependentLifeInformationReq);

        return new CounselCardIdRes(counselCard.getId());
    }

    @Transactional
    public CounselCardIdRes updateCounselCardLivingInformation(String counselSessionId,
        UpdateLivingInformationReq updateLivingInformationReq) {
        CounselCard counselCard = counselCardRepository.findCounselCardByCounselSessionId(
                counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        counselCard.updateLiving(updateLivingInformationReq);

        return new CounselCardIdRes(counselCard.getId());
    }

    @Transactional
    public CounselCardIdRes deleteCounselCard(String counselSessionId) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardWithCounselee(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        counselCardRepository.delete(counselCard);

        return new CounselCardIdRes(counselCard.getId());
    }

    public <R> List<TimeRecordedRes<R>> selectPreviousRecordsByType(String counselSessionId,
        CounselCardRecordType type) {
        return selectPreviousRecordByCounselSessionId(counselSessionId, type.getExtractor(),
            type.getDtoConverter());
    }

    private <T, R> List<TimeRecordedRes<R>> selectPreviousRecordByCounselSessionId(
        String counselSessionId,
        Function<CounselCard, T> extractor,
        Function<T, R> dtoConverter) {

        return selectRecordedCardsByPreviousSessions(counselSessionId)
            .stream()
            .map(counselCard -> new TimeRecordedRes<>(
                counselCard.getCounselSession().getScheduledStartDateTime().toLocalDate()
                    .toString(),
                dtoConverter.apply(extractor.apply(counselCard))))
            .toList();
    }

    private List<CounselCard> selectRecordedCardsByPreviousSessions(String counselSessionId) {
        List<CounselCard> recordedCardsByPreviousSessions = counselCardRepository.findRecordedCardsByPreviousSessions(
            counselSessionId);

        if (recordedCardsByPreviousSessions.isEmpty()) {
            throw new NoContentException();
        }

        return recordedCardsByPreviousSessions;
    }
}
