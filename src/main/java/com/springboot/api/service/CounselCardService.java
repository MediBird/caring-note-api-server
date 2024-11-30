package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.dto.counselcard.*;
import com.springboot.api.repository.CounselCardRepository;
import com.springboot.api.repository.CounselSessionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CounselCardService {


    private final CounselCardRepository counselCardRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final EntityManager entityManager;

    public CounselCardService(CounselCardRepository counselCardRepository
            , CounselSessionRepository counselSessionRepository
            , EntityManager entityManager) {
        this.counselCardRepository = counselCardRepository;
        this.counselSessionRepository = counselSessionRepository;
        this.entityManager = entityManager;
    }

    public SelectCounselCardRes selectCounselCard(String id, String counselSessionId) throws RuntimeException{

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(NoContentException::new);

        CounselCard counselCard = Optional.ofNullable(counselSession.getCounselCard())
                .orElseThrow(NoContentException::new);


        return new SelectCounselCardRes(counselCard.getId()
                ,counselCard.getBaseInformation()
                ,counselCard.getHealthInformation()
                ,counselCard.getLivingInformation()
                ,counselCard.getSelfReliantLivingInformation()
        );
    }

    public SelectPreviousCounselCardRes selectPreviousCounselCard(String id, String counselSessionId) throws RuntimeException{

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
                ,previousCounselCard.getSelfReliantLivingInformation()
        );
    }



    @Transactional
    public InsertCounselCardRes insertCounselCard(String id, InsertCounselCardReq insertCounselCardReq) throws RuntimeException
    {
        CounselSession counselSessionProxy = entityManager.getReference(CounselSession.class, insertCounselCardReq.getCounselSessionId());

        CounselCard counselCard = CounselCard.builder()
                .counselSession(counselSessionProxy)
                .baseInformation(insertCounselCardReq.getBaseInformation())
                .healthInformation(insertCounselCardReq.getHealthInformation())
                .livingInformation(insertCounselCardReq.getLivingInformation())
                .selfReliantLivingInformation(insertCounselCardReq.getSelfReliantLivingInformation())
                .build();

        CounselCard savedCounselCard = counselCardRepository.save(counselCard);

        return new InsertCounselCardRes(savedCounselCard.getId());

    }

    @Transactional
    public UpdateCounselCardRes updateCounselCard(String id, UpdateCounselCardReq updateCounselCardReq) throws RuntimeException
    {
        CounselCard counselCard = counselCardRepository.findById(updateCounselCardReq.getCounselCardId())
                .orElseThrow(NoContentException::new);

        counselCard.setBaseInformation(updateCounselCardReq.getBaseInformation());
        counselCard.setHealthInformation(updateCounselCardReq.getHealthInformation());
        counselCard.setLivingInformation(updateCounselCardReq.getLivingInformation());
        counselCard.setSelfReliantLivingInformation(updateCounselCardReq.getSelfReliantLivingInformation());

        return new UpdateCounselCardRes(counselCard.getId());
    }


}
