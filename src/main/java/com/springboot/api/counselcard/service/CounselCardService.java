package com.springboot.api.counselcard.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.counselcard.dto.AddCounselCardReq;
import com.springboot.api.counselcard.dto.CounselCardIdRes;
import com.springboot.api.counselcard.dto.CounselCardRes;
import com.springboot.api.counselcard.dto.TimeRecordedDTO;
import com.springboot.api.counselcard.dto.UpdateCounselCardReq;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.enums.CounselCardRecordType;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.api.counselcard.repository.CounselCardRepository;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounselCardService {

    private final CounselCardRepository counselCardRepository;
    private final CounselSessionRepository counselSessionRepository;

    public CounselCardRes selectCounselCard(String counselSessionId) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardWithCounselee(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        return new CounselCardRes(counselCard);
    }

    public CounselCardRes selectLastRecordedCounselCard(String counseleeId) {
        CounselCard previousCounselCard = counselCardRepository
            .findLastRecordedCounselCard(counseleeId)
            .orElseThrow(IllegalArgumentException::new);

        return new CounselCardRes(previousCounselCard);
    }

    @Transactional
    public CounselCardIdRes addCounselCard(AddCounselCardReq addCounselCardReq) {
        CounselSession counselSession = counselSessionRepository
            .findByIdWithCounselee(addCounselCardReq.counselSessionId())
            .orElseThrow(IllegalArgumentException::new);

        if(counselCardRepository.existsByCounselSessionId(addCounselCardReq.counselSessionId())) {
            throw new IllegalArgumentException();
        }

        CounselCard counselCard = CounselCard.of(counselSession, addCounselCardReq);

        return new CounselCardIdRes(counselCardRepository.save(counselCard).getId());
    }

    @Transactional
    public CounselCardIdRes updateCounselCard(UpdateCounselCardReq updateCounselCardReq) {
        CounselCard counselCard = counselCardRepository
            .findCounselCardWithCounselee(updateCounselCardReq.counselSessionId())
            .orElseThrow(IllegalArgumentException::new);

        counselCard.update(updateCounselCardReq);

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

    public <R> List<TimeRecordedDTO<R>> selectPreviousRecordsByType(String counselSessionId, CounselCardRecordType type) {
        return selectPreviousRecordByCounselSessionId(counselSessionId, type.getExtractor(), type.getDtoConverter());
    }

    private <T, R> List<TimeRecordedDTO<R>> selectPreviousRecordByCounselSessionId(
        String counselSessionId,
        Function<CounselCard, T> extractor,
        Function<T, R> dtoConverter) {

        return selectRecordedCardsByPreviousSessions(counselSessionId)
            .stream()
            .map(counselCard -> new TimeRecordedDTO<>(
                counselCard.getCounselSession().getScheduledStartDateTime().toLocalDate().toString(),
                dtoConverter.apply(extractor.apply(counselCard))))
            .toList();
    }

    private List<CounselCard> selectRecordedCardsByPreviousSessions(String counselSessionId){
        List<CounselCard> recordedCardsByPreviousSessions = counselCardRepository.findRecordedCardsByPreviousSessions(
            counselSessionId);

        if(recordedCardsByPreviousSessions.isEmpty()){
            throw new NoContentException();
        }

        return recordedCardsByPreviousSessions;
    }
}
