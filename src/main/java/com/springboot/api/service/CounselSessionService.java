package com.springboot.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.BaseEntity;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselsession.*;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.enums.RoleType;
import com.springboot.enums.ScheduleStatus;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CounselSessionService {

    private final CounselSessionRepository sessionRepository;
    private final EntityManager entityManager;
    private final DateTimeUtil dateTimeUtil;


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

    public SelectCounselSessionRes selectCounselSession(String id) throws RuntimeException
    {
        CounselSession counselSession = sessionRepository.findById(id).orElseThrow(
                NoContentException::new
        );

        return SelectCounselSessionRes
                .builder()
                .counselSessionId(counselSession.getId())
                .scheduledTime(counselSession.getScheduledStartDateTime().toLocalDate().toString())
                .scheduledDate(counselSession.getScheduledStartDateTime().toLocalTime().toString())
                .counseleeId(Optional.ofNullable(counselSession.getCounselee())
                        .map(Counselee::getId)
                        .orElse(""))
                .counseleeName(Optional.ofNullable(counselSession.getCounselee())
                        .map(Counselee::getName)
                        .orElse(""))
                .counselorId(Optional.ofNullable(counselSession.getCounselor())
                        .map(Counselor::getId)
                        .orElse(""))
                .counselorName(Optional.ofNullable(counselSession.getCounselor())
                        .map(Counselor::getName)
                        .orElse(""))
                .build();

    }

    public SelectCounselSessionListRes selectCounselSessionList(String id, RoleType roleType, SelectCounselSessionListReq selectCounselSessionListReq) throws RuntimeException
    {
        Pageable pageable = PageRequest.of(0, selectCounselSessionListReq.getSize());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<CounselSession> sessions;


        sessions = sessionRepository.findByCursor(
                    Optional.ofNullable(selectCounselSessionListReq.getBaseDate())
                        .map(LocalDate::atStartOfDay)
                        .orElse(null)
                    ,Optional.ofNullable(selectCounselSessionListReq.getBaseDate())
                        .map(d->d.plusDays(1))
                        .map(LocalDate::atStartOfDay)
                        .orElse(null)
                    , selectCounselSessionListReq.getCursor()
                    , null
                    , pageable);


        String nextCursorId = null;

        if (!sessions.isEmpty()) {
            CounselSession lastSession = sessions.getLast();
            nextCursorId = lastSession.getId();
            }

            // hasNext 계산
            boolean hasNext = sessions.size() == selectCounselSessionListReq.getSize();

            List<SelectCounselSessionListItem> sessionListItems = sessions.stream()
                    .map(s-> SelectCounselSessionListItem.builder()
                            .counselorName(Optional.ofNullable(s.getCounselor())
                                    .map(Counselor::getName)
                                    .orElse(""))
                            .counseleeId(Optional.ofNullable(s.getCounselee())
                                    .map(BaseEntity::getId)
                                    .orElse(""))
                            .counseleeName(Optional.ofNullable(s.getCounselee())
                                    .map(Counselee::getName)
                                    .orElse(""))
                            .isShardCaringMessage(false)
                            .scheduledDate(s.getScheduledStartDateTime().toLocalDate().toString())
                            .scheduledTime(s.getScheduledStartDateTime().toLocalTime().format(timeFormatter))
                            .counselSessionId(s.getId())
                            .build())
                    .toList();

            return new SelectCounselSessionListRes(sessionListItems,nextCursorId,hasNext);

        }



    @Transactional
    public UpdateCounselSessionRes updateCounselSession(UpdateCounselSessionReq updateCounselSessionReq) throws RuntimeException
    {
        CounselSession counselSession = sessionRepository.findById(updateCounselSessionReq.getCounselSessionId()).orElseThrow(
                NoContentException::new
        );

        Counselor proxyCounselor = entityManager.getReference(Counselor.class, updateCounselSessionReq.getCounselorId());
        Counselee proxyCounselee = entityManager.getReference(Counselee.class, updateCounselSessionReq.getCounseleeId());
        counselSession.setScheduledStartDateTime(dateTimeUtil
                .parseToDateTime(updateCounselSessionReq.getScheduledStartDateTime()));
        counselSession.setCounselor(proxyCounselor);
        counselSession.setCounselee(proxyCounselee);


        return new UpdateCounselSessionRes(updateCounselSessionReq.getCounselSessionId());
    }

    @Transactional
    public DeleteCounselSessionRes deleteCounselSessionRes(DeleteCounselSessionReq deleteCounselSessionReq)
    {

        sessionRepository.deleteById(deleteCounselSessionReq.getCounselSessionId());

        return new DeleteCounselSessionRes(deleteCounselSessionReq.getCounselSessionId());

    }



    public List<SelectPreviousListByCounselSessionIdRes> selectPreviousListByCounselSessionId(String id, String counselSessionId)
    {
        CounselSession counselSession = sessionRepository.findById(counselSessionId)
                .orElseThrow(NoContentException::new);

        Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
                .orElseThrow(NoContentException::new);

        List<CounselSession> previousCounselSessions = sessionRepository.findByCounseleeIdAndScheduledStartDateTimeLessThan(counselee.getId(),counselSession.getScheduledStartDateTime());


        return previousCounselSessions
                .stream()
                .filter(session -> ScheduleStatus.COMPLETED.equals(session.getStatus()))
                .map(session -> {

                    JsonNode baseInfo = session.getCounselCard().getBaseInformation().get("baseInfo");
                    return new SelectPreviousListByCounselSessionIdRes(
                            session.getId()
                            , baseInfo.get("counselSessionOrder").asText()
                            , session.getScheduledStartDateTime().toLocalDate()
                            , session.getCounselor().getName()
                            , false
                    );
                })
                .toList();

    }






}
