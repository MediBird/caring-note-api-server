package com.springboot.api.service;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselsession.*;
import com.springboot.api.repository.CounselSessionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class CounselSessionService {

    private final CounselSessionRepository sessionRepository;
    private final EntityManager entityManager;
    private final DateTimeUtil dateTimeUtil;

    public CounselSessionService(CounselSessionRepository sessionRepository, EntityManager entityManager, DateTimeUtil dateTimeUtil) {
        this.sessionRepository = sessionRepository;
        this.entityManager = entityManager;
        this.dateTimeUtil = dateTimeUtil;
    }


    public AddCounselSessionRes addCounselSession(String id, AddCounselSessionReq addCounselSessionReq) throws RuntimeException
    {
        Counselor proxyCounselor = entityManager.getReference(Counselor.class, id);
        Counselee proxyCounselee = entityManager.getReference(Counselee.class, addCounselSessionReq.getCounseleeId());

        CounselSession counselSession = CounselSession.builder()
                .counselor(proxyCounselor)
                .counselee(proxyCounselee)
                .scheduledStartDateTime(dateTimeUtil
                        .parseToDateTime(addCounselSessionReq.getScheduledStartDateTime()))
                .status(addCounselSessionReq.getStatus())
                .build();

        CounselSession savedCounselSession = sessionRepository.save(counselSession);

        return new AddCounselSessionRes(savedCounselSession.getId());
    }

//    public SelectCounselSessionRes selectCounselSession()
//    {
//
//    }
//
//    public SelectCounselSessionListRes selectCounselSessionList()
//    {
//
//    }
//
//    public UpdateCounselSessionRes updateCounselSession(UpdateCounselSessionReq updateCounselSessionReq)
//    {
//
//    }
//
//    public DeleteCounselSessionRes deleteCounselSessionRes(DeleteCounselSessionReq deleteCounselSessionReq)
//    {
//
//    }






}
