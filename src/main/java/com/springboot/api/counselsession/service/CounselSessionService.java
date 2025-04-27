package com.springboot.api.counselsession.service;

import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.counselcard.service.CounselCardService;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselor.service.CounselorService;
import com.springboot.api.counselsession.dto.counselsession.CounselSessionStatRes;
import com.springboot.api.counselsession.dto.counselsession.CreateCounselReservationReq;
import com.springboot.api.counselsession.dto.counselsession.CreateCounselReservationRes;
import com.springboot.api.counselsession.dto.counselsession.DeleteCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.DeleteCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.ModifyCounselReservationReq;
import com.springboot.api.counselsession.dto.counselsession.ModifyCounselReservationRes;
import com.springboot.api.counselsession.dto.counselsession.SearchCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.SelectPreviousCounselSessionListRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselorInCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselorInCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateStatusInCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateStatusInCounselSessionRes;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.enums.ScheduleStatus;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CounselSessionService {

    private final DateTimeUtil dateTimeUtil;
    private final CounselSessionRepository counselSessionRepository;
    private final CounselorService counselorService;
    private final CounseleeRepository counseleeRepository;
    private final CounselCardService counselCardService;
    private final CounseleeConsentService counseleeConsentService;

    @CacheEvict(value = {"sessionDates", "sessionStats", "sessionList"}, allEntries = true)
    @Transactional
    public CreateCounselReservationRes createReservation(CreateCounselReservationReq createReservationReq) {
        LocalDateTime scheduledStartDateTime = dateTimeUtil
            .parseToDateTime(createReservationReq.getScheduledStartDateTime());

        Counselee counselee = findAndValidateCounseleeSchedule(createReservationReq.getCounseleeId(),
            scheduledStartDateTime);

        CounselSession counselSession = CounselSession.createReservation(
            counselee,
            scheduledStartDateTime);
        CounselSession savedCounselSession = counselSessionRepository.save(counselSession);

        counselCardService.initializeCounselCard(savedCounselSession);
        counseleeConsentService.initializeCounseleeConsent(counselSession, counselee);
        reassignSessionNumbers(createReservationReq.getCounseleeId());

        return new CreateCounselReservationRes(savedCounselSession.getId());
    }

    @CacheEvict(value = {"sessionDates", "sessionStats", "sessionList"}, allEntries = true)
    @Transactional
    public ModifyCounselReservationRes modifyCounselReservation(
        ModifyCounselReservationReq modifyCounselReservationReq) {
        CounselSession counselSession = counselSessionRepository
            .findById(modifyCounselReservationReq.getCounselSessionId()).orElseThrow(
                NoContentException::new);

        LocalDateTime scheduledStartDateTime = dateTimeUtil
            .parseToDateTime(modifyCounselReservationReq.getScheduledStartDateTime());

        Counselee counselee = findAndValidateCounseleeSchedule(modifyCounselReservationReq.getCounseleeId(),
            scheduledStartDateTime);

        counselSession.modifyReservation(scheduledStartDateTime, counselee);

        reassignSessionNumbers(modifyCounselReservationReq.getCounseleeId());

        return new ModifyCounselReservationRes(modifyCounselReservationReq.getCounselSessionId());
    }

    private Counselee findAndValidateCounseleeSchedule(String counseleeId, LocalDateTime scheduledStartDateTime) {
        Counselee counselee = counseleeRepository.findById(counseleeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 내담자 ID입니다"));

        if (counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee,
            scheduledStartDateTime)) {
            throw new IllegalArgumentException("해당 시간에 이미 상담이 예약되어 있습니다");
        }

        return counselee;
    }

    public SelectCounselSessionRes selectCounselSession(String id) {
        CounselSession counselSession = counselSessionRepository.findById(id).orElseThrow(
            IllegalArgumentException::new);

        return SelectCounselSessionRes.from(counselSession);
    }

    @Cacheable(value = "sessionList", key = "#baseDate + '-' + #req.page + '-' + #req.size")
    @Transactional(readOnly = true)
    public PageRes<SelectCounselSessionListItem> selectCounselSessionListByBaseDate(PageReq req,
        LocalDate baseDate) {

        return counselSessionRepository.findSessionByCursorAndDate(baseDate, req);
    }

    @CacheEvict(value = {"sessionList"}, allEntries = true)
    @Transactional
    public UpdateCounselorInCounselSessionRes updateCounselorInCounselSession(
        UpdateCounselorInCounselSessionReq updateCounselorInCounselSessionReq) {
        CounselSession counselSession = counselSessionRepository
            .findById(updateCounselorInCounselSessionReq.counselSessionId())
            .orElseThrow(NoContentException::new);

        Counselor counselor = counselorService.findCounselorById(updateCounselorInCounselSessionReq.counselorId());

        counselSession.updateCounselor(counselor);

        return new UpdateCounselorInCounselSessionRes(counselSession.getId());
    }

    @CacheEvict(value = {"sessionStats", "sessionList"}, allEntries = true)
    @Transactional
    public UpdateStatusInCounselSessionRes updateCounselSessionStatus(
        UpdateStatusInCounselSessionReq updateStatusInCounselSessionReq) {

        // TODO 쿼리 최적화 고려
        CounselSession counselSession = counselSessionRepository.findById(
                updateStatusInCounselSessionReq.counselSessionId())
            .orElseThrow(NoContentException::new);

        if (counselSession.getStatus() == ScheduleStatus.COMPLETED) {
            throw new IllegalArgumentException("완료된 상담 세션은 상태를 변경할 수 없습니다.");
        }

        if (counselSession.getStatus() == ScheduleStatus.CANCELED) {
            throw new IllegalArgumentException("취소된 상담 세션은 상태를 변경할 수 없습니다.");
        }

        switch (updateStatusInCounselSessionReq.status()) {
            case COMPLETED -> counselSession.completeCounselSession();
            case IN_PROGRESS -> counselSession.progressCounselSession();
            case CANCELED -> counselSession.cancelCounselSession();
            case SCHEDULED -> counselSession.scheduleCounselSession();
        }

        reassignSessionNumbers(counselSession.getCounselee().getId());

        return new UpdateStatusInCounselSessionRes(counselSession.getId());
    }

    @CacheEvict(value = {"sessionDates", "sessionStats", "sessionList"}, allEntries = true)
    @Transactional
    public DeleteCounselSessionRes deleteCounselSessionRes(DeleteCounselSessionReq deleteCounselSessionReq) {
        CounselSession counselSession = counselSessionRepository.findById(
                deleteCounselSessionReq.getCounselSessionId())
            .orElseThrow(IllegalArgumentException::new);

        counselSessionRepository.delete(counselSession);

        return new DeleteCounselSessionRes(counselSession.getId());
    }

    public List<SelectPreviousCounselSessionListRes> selectPreviousCounselSessionList(String counselSessionId) {
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
            .orElseThrow(NoContentException::new);

        List<CounselSession> previousCounselSessions = counselSessionRepository
            .findPreviousCompletedSessionsOrderByEndDateTimeDesc(counselee.getId(),
                counselSession.getScheduledStartDateTime());

        List<SelectPreviousCounselSessionListRes> selectPreviousCounselSessionListResList = previousCounselSessions
            .stream()
            .map(session -> new SelectPreviousCounselSessionListRes(
                session.getId(),
                session.getSessionNumber(),
                session.getScheduledStartDateTime().toLocalDate(),
                session.getCounselor().getName(),
                false))
            .toList();

        if (selectPreviousCounselSessionListResList.isEmpty()) {
            throw new NoContentException();
        }

        return selectPreviousCounselSessionListResList;

    }

    @Cacheable(value = "sessionDates", key = "#year + '-' + #month")
    public List<LocalDate> getSessionDatesByYearAndMonth(int year, int month) {
        return counselSessionRepository.findDistinctDatesByYearAndMonth(year, month);
    }

    @Cacheable(value = "sessionStats")
    @Transactional(readOnly = true)
    public CounselSessionStatRes getSessionStats() {
        Long totalSessionCount = calculateTotalSessionCount();
        Long counseleeCount = counselSessionRepository.countDistinctCounseleeForCurrentMonth();
        Long totalCaringMessageCount = counselSessionRepository.count();
        double counselHours = calculateCounselHoursForThisMonth();

        return CounselSessionStatRes.builder()
            .totalSessionCount(totalSessionCount)
            .counseleeCountForThisMonth(counseleeCount)
            .totalCaringMessageCount(totalCaringMessageCount)
            .counselHoursForThisMonth((long) counselHours)
            .build();
    }

    private long calculateTotalSessionCount() {
        return counselSessionRepository.countByStatus(ScheduleStatus.COMPLETED);
    }

    private double calculateCounselHoursForThisMonth() {
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        List<CounselSession> completedSessions = counselSessionRepository
            .findCompletedSessionsByYearAndMonth(year, month);

        return completedSessions.stream()
            .mapToDouble(session -> {
                Duration duration = Duration.between(
                    session.getStartDateTime(),
                    session.getEndDateTime());
                return duration.toMinutes() / 60.0;
            })
            .sum();
    }

    @Scheduled(cron = "0 0 * * * *") // 매시간 실행
    @Transactional
    public void cancelOverdueSessions() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Checking for overdue sessions at {}", now);
        List<String> affectedCounseleeIds = counselSessionRepository.cancelOverDueSessionsAndReturnAffectedCounseleeIds();
        log.info("취소된 세션으로 인해 영향을 받은 대상 수: {}", affectedCounseleeIds.size());

        for (String counseleeId : affectedCounseleeIds) {
            reassignSessionNumbers(counseleeId); // 회차 재정렬
        }
    }

    @Transactional(readOnly = true)
    public PageRes<SelectCounselSessionRes> searchCounselSessions(SearchCounselSessionReq req) {
        PageRes<CounselSession> counselSessionPageRes = counselSessionRepository
            .findByCounseleeNameAndCounselorNameAndScheduledDateTime(
                req.pageReq(),
                req.counseleeNameKeyword(),
                req.counselorNames(),
                req.scheduledDates()
            );

        return counselSessionPageRes.map(SelectCounselSessionRes::from);
    }

    public void reassignSessionNumbers(String counseleeId) {
        List<CounselSession> counselSessions = counselSessionRepository.findValidCounselSessionsByCounseleeId(
            counseleeId);

        Map<String, Integer> sessionUpdates = new HashMap<>();

        int sessionNumber = 1;
        for (CounselSession session : counselSessions) {
            if (!Objects.equals(session.getSessionNumber(), sessionNumber)) {
                sessionUpdates.put(session.getId(), sessionNumber);
            }
            sessionNumber++;
        }

        if (!sessionUpdates.isEmpty()) {
            counselSessionRepository.bulkUpdateCounselSessionNum(sessionUpdates);
        }
    }
}
