package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.dto.counselcard.InsertCounselCardReq;
import com.springboot.api.dto.counselcard.InsertCounselCardRes;
import com.springboot.api.dto.counselcard.UpdateCounselCardReq;
import com.springboot.api.dto.counselcard.UpdateCounselCardRes;
import com.springboot.api.repository.CounselCardRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CounselCardService {


    private final CounselCardRepository counselCardRepository;
    private final EntityManager entityManager;
    public CounselCardService(CounselCardRepository counselCardRepository, EntityManager entityManager) {
        this.counselCardRepository = counselCardRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public InsertCounselCardRes insertCounselCard(String id, InsertCounselCardReq insertCounselCardReq)
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
    public UpdateCounselCardRes updateCounselCard(String id, UpdateCounselCardReq updateCounselCardReq)
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
