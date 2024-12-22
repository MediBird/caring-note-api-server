package com.springboot.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.dto.counselcard.*;
import com.springboot.api.repository.CounselCardRepository;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.enums.ScheduleStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CounselCardService {


    private final CounselCardRepository counselCardRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final EntityManager entityManager;


    public SelectCounselCardRes selectCounselCard(String counselSessionId){

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(NoContentException::new);

        CounselCard counselCard = Optional.ofNullable(counselSession.getCounselCard())
                .orElseThrow(NoContentException::new);


        return new SelectCounselCardRes(counselCard.getId()
                ,counselCard.getBaseInformation()
                ,counselCard.getHealthInformation()
                ,counselCard.getLivingInformation()
                ,counselCard.getIndependentLifeInformation()
                ,counselCard.getCardRecordStatus()
        );
    }

    public SelectPreviousCounselCardRes selectPreviousCounselCard(String counselSessionId){

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(NoContentException::new);

        Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
                .orElseThrow(NoContentException::new);

        Pageable pageable = PageRequest.of(0, 1);

       List<CounselSession> previousCounselSessions = counselSessionRepository.findByCounseleeIdPrevious(counselSessionId,counselee.getId(), pageable);

        Optional.of(previousCounselSessions)
                .filter(sessions -> !sessions.isEmpty())
                .orElseThrow(NoContentException::new);

        CounselCard previousCounselCard = Optional.ofNullable(previousCounselSessions.getFirst().getCounselCard())
                .orElseThrow(NoContentException::new);


        return new SelectPreviousCounselCardRes(previousCounselCard.getBaseInformation()
                ,previousCounselCard.getHealthInformation()
                ,previousCounselCard.getLivingInformation()
                ,previousCounselCard.getIndependentLifeInformation()
        );
    }



    @Transactional
    public AddCounselCardRes addCounselCard(AddCounselCardReq addCounselCardReq)
    {
        CounselSession counselSessionProxy = entityManager.getReference(CounselSession.class, addCounselCardReq.getCounselSessionId());

        CounselCard counselCard = CounselCard.builder()
                .counselSession(counselSessionProxy)
                .baseInformation(addCounselCardReq.getBaseInformation())
                .healthInformation(addCounselCardReq.getHealthInformation())
                .livingInformation(addCounselCardReq.getLivingInformation())
                .independentLifeInformation(addCounselCardReq.getIndependentLifeInformation())
                .cardRecordStatus(addCounselCardReq.getCardRecordStatus())
                .build();

        CounselCard savedCounselCard = counselCardRepository.save(counselCard);

        return new AddCounselCardRes(savedCounselCard.getId());

    }

    @Transactional
    public UpdateCounselCardRes updateCounselCard(UpdateCounselCardReq updateCounselCardReq)
    {
        CounselCard counselCard = counselCardRepository.findById(updateCounselCardReq.getCounselCardId())
                .orElseThrow(NoContentException::new);

        counselCard.setBaseInformation(updateCounselCardReq.getBaseInformation());
        counselCard.setHealthInformation(updateCounselCardReq.getHealthInformation());
        counselCard.setLivingInformation(updateCounselCardReq.getLivingInformation());
        counselCard.setIndependentLifeInformation(updateCounselCardReq.getIndependentLifeInformation());
        counselCard.setCardRecordStatus(updateCounselCardReq.getCardRecordStatus());

        return new UpdateCounselCardRes(counselCard.getId());
    }

    @Transactional
    public DeleteCounselCardRes deleteCounselCard(DeleteCounselCardReq deleteCounselCardReq)
    {
        if(!counselCardRepository.existsById(deleteCounselCardReq.getCounselCardId())){
            throw new NoContentException();
        }
        counselCardRepository.deleteById(deleteCounselCardReq.getCounselCardId());
        return new DeleteCounselCardRes(deleteCounselCardReq.getCounselCardId());
    }


    public List<SelectPreviousItemListByInformationNameAndItemNameRes> selectPreviousItemListByInformationNameAndItemName(
            String counselSessionId
            , String informationName
            , String itemName){

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(NoContentException::new);

        Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
                .orElseThrow(NoContentException::new);

        List<CounselSession> previousCounselSessions =
                counselSessionRepository.findByCounseleeIdAndScheduledStartDateTimeLessThan(
                        counselee.getId()
                        ,counselSession.getScheduledStartDateTime()
                );

        return previousCounselSessions
                .stream()
                .filter(cs -> ScheduleStatus.COMPLETED.equals(cs.getStatus()))
                .map(cs -> {
                    JsonNode informationJsonNode;

                    switch (informationName) {
                        case "baseInformation" -> informationJsonNode = cs.getCounselCard().getBaseInformation();
                        case "healthInformation" -> informationJsonNode = cs.getCounselCard().getHealthInformation();
                        case "livingInformation" -> informationJsonNode = cs.getCounselCard().getLivingInformation();
                        default -> throw new NoContentException();
                    }
                    return new AbstractMap.SimpleEntry<>(cs, informationJsonNode);
                })
                .map(entry -> {
                    JsonNode informationJsonNode = entry.getValue();
                    JsonNode itemNode = Optional.ofNullable(informationJsonNode.get(itemName))
                            .orElseThrow(NoContentException::new);

                    return new SelectPreviousItemListByInformationNameAndItemNameRes( entry.getKey().getScheduledStartDateTime().toLocalDate()
                            ,itemNode);
                })
                .toList();

    }


}
