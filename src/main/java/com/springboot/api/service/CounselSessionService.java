package com.springboot.api.service;

import com.springboot.api.common.exception.ResourceNotFoundException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselsession.*;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.enums.RoleType;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    @Transactional
    public AddCounselSessionRes addCounselSession(String id, AddCounselSessionReq addCounselSessionReq) throws RuntimeException
    {
        Counselor proxyCounselor = entityManager.getReference(Counselor.class, addCounselSessionReq.getCounselorId());
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
    public SelectCounselSessionListRes selectCounselSessionList(String id, RoleType roleType, SelectCounselSessionListReq selectCounselSessionListReq) throws RuntimeException
    {
        Pageable pageable = PageRequest.of(0, selectCounselSessionListReq.getSize());

        List<CounselSession> sessions = null;

        if(roleType == RoleType.ADMIN)
        {
            sessions = sessionRepository.findByCursor(selectCounselSessionListReq.getBaseDateTime()
                    , selectCounselSessionListReq.getCursorId()
                    , null
                    , pageable);

        }
        else {// 커서 기반에서는 항상 첫 페이지 사용
            sessions = sessionRepository.findByCursor(selectCounselSessionListReq.getBaseDateTime()
                    , selectCounselSessionListReq.getCursorId()
                    , id
                    , pageable);
        }

        String nextCursorId = null;

        if (!sessions.isEmpty()) {
            CounselSession lastSession = sessions.getLast();
            nextCursorId = lastSession.getId();
            }

            // hasNext 계산
            boolean hasNext = sessions.size() == selectCounselSessionListReq.getSize();

            List<SelectCounselSessionListItem> sessionListItems = sessions.stream()
                    .map(s->{
                        return SelectCounselSessionListItem.builder()
                                .counselorName(Optional.ofNullable(s.getCounselee())
                                        .map(Counselee::getName)
                                        .orElse(""))
                                .counseleeName(Optional.ofNullable(s.getCounselor())
                                        .map(Counselor::getName)
                                        .orElse(""))
                                .isShardCaringMessage(false)
                                .scheduledDate(s.getScheduledStartDateTime().toLocalDate().toString())
                                .scheduledTime(s.getScheduledStartDateTime().toLocalTime().toString())
                                .id(s.getId())
                                .build();
                    })
                    .toList();

            return new SelectCounselSessionListRes(sessionListItems,nextCursorId,hasNext);

        }


    @Transactional
    public UpdateCounselSessionRes updateCounselSession(UpdateCounselSessionReq updateCounselSessionReq) throws RuntimeException
    {
        CounselSession counselSession = sessionRepository.findById(updateCounselSessionReq.getId()).orElseThrow(
                ResourceNotFoundException::new
        );

        Counselor proxyCounselor = entityManager.getReference(Counselor.class, updateCounselSessionReq.getCounselorId());
        Counselee proxyCounselee = entityManager.getReference(Counselee.class, updateCounselSessionReq.getCounseleeId());
        counselSession.setScheduledStartDateTime(dateTimeUtil
                .parseToDateTime(updateCounselSessionReq.getScheduledStartDateTime()));
        counselSession.setCounselor(proxyCounselor);
        counselSession.setCounselee(proxyCounselee);


        return new UpdateCounselSessionRes(updateCounselSessionReq.getId());
    }

    @Transactional
    public DeleteCounselSessionRes deleteCounselSessionRes(DeleteCounselSessionReq deleteCounselSessionReq)
    {

        sessionRepository.deleteById(deleteCounselSessionReq.getId());

        return new DeleteCounselSessionRes(deleteCounselSessionReq.getId());

    }






}
