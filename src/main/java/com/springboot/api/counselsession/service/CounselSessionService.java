package com.springboot.api.counselsession.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.AiResponseParseUtil;
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
import com.springboot.api.counselsession.dto.counselsession.SelectPreviousCounselSessionDetailRes;
import com.springboot.api.counselsession.dto.counselsession.SelectPreviousCounselSessionListRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselorInCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselorInCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateStatusInCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateStatusInCounselSessionRes;
import com.springboot.api.counselsession.entity.AICounselSummary;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.MedicationCounsel;
import com.springboot.api.counselsession.repository.AICounselSummaryRepository;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.repository.MedicationCounselRepository;
import com.springboot.enums.ScheduleStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final MedicationCounselRepository medicationCounselRepository;
    private final AICounselSummaryRepository aiCounselSummaryRepository;
    private final AiResponseParseUtil aiResponseParseUtil;

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

    @Transactional(readOnly = true)
    public PageRes<SelectPreviousCounselSessionDetailRes> selectPreviousCounselSessionDetailList(
        String counselSessionId, PageReq pageReq) {
        
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담 세션입니다."));

        Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
            .orElseThrow(() -> new NoContentException("내담자 정보를 찾을 수 없습니다."));

        // 이전 완료된 상담 세션들을 최신 순으로 조회
        List<CounselSession> previousCounselSessions = counselSessionRepository
            .findPreviousCompletedSessionsOrderByEndDateTimeDesc(counselee.getId(),
                counselSession.getScheduledStartDateTime());

        if (previousCounselSessions.isEmpty()) {
            throw new NoContentException("이전 상담 내역이 없습니다.");
        }

        // 페이징 처리
        int totalElements = previousCounselSessions.size();
        int startIndex = pageReq.getPage() * pageReq.getSize();
        int endIndex = Math.min(startIndex + pageReq.getSize(), totalElements);

        if (startIndex >= totalElements) {
            throw new NoContentException("요청한 페이지에 데이터가 없습니다.");
        }

        List<CounselSession> pagedSessions = previousCounselSessions.subList(startIndex, endIndex);

        // 각 세션에 대한 상세 정보 조회
        List<SelectPreviousCounselSessionDetailRes> detailList = pagedSessions.stream()
            .map(session -> {
                // 중재기록 조회
                Optional<MedicationCounsel> medicationCounsel = medicationCounselRepository
                    .findByCounselSessionId(session.getId());
                String counselRecord = medicationCounsel
                    .map(MedicationCounsel::getCounselRecord)
                    .orElse(null);

                // AI 요약 조회
                Optional<AICounselSummary> aiCounselSummary = aiCounselSummaryRepository
                    .findByCounselSessionId(session.getId());
                String aiSummaryText = aiCounselSummary
                    .map(AICounselSummary::getTaResult)
                    .flatMap(aiResponseParseUtil::extractAnalysedTextSafely)
                    .orElse(null);

                String counselorName = Optional.ofNullable(session.getCounselor())
                    .map(Counselor::getName)
                    .orElse("미지정");

                return SelectPreviousCounselSessionDetailRes.builder()
                    .counselSessionId(session.getId())
                    .counselSessionDate(session.getScheduledStartDateTime().toLocalDate())
                    .sessionNumber(session.getSessionNumber())
                    .counselorName(counselorName)
                    .medicationCounselRecord(counselRecord)
                    .aiSummary(aiSummaryText)
                    .build();
            })
            .toList();

        Page<SelectPreviousCounselSessionDetailRes> page = new PageImpl<>(
            detailList, pageReq.toPageable(), totalElements);
        
        return new PageRes<>(page);
    }

    @Cacheable(value = "sessionDates", key = "#year + '-' + #month")
    public List<LocalDate> getSessionDatesByYearAndMonth(int year, int month) {
        return counselSessionRepository.findDistinctDatesByYearAndMonth(year, month);
    }

    @Cacheable(value = "sessionStats")
    @Transactional(readOnly = true)
    public CounselSessionStatRes getSessionStats() {
        long counselHoursThisMonth = calculateCounselHoursForThisMonth();
        Long counseleeCountForThisMonth = counselSessionRepository.countDistinctCounseleeForCurrentMonth();
        long medicationCounselCountThisYear = calculateMedicationCounselCountThisYear();
        long counselorCountThisYear = calculateCounselorCountThisYear();

        return CounselSessionStatRes.builder()
            .counselHoursThisMonth(counselHoursThisMonth)
            .counseleeCountForThisMonth(counseleeCountForThisMonth)
            .medicationCounselCountThisYear(medicationCounselCountThisYear)
            .counselorCountThisYear(counselorCountThisYear)
            .build();
    }

    private long calculateMedicationCounselCountThisYear() {
        int currentYear = LocalDateTime.now().getYear();
        return medicationCounselRepository.countByCreatedDatetimeBetween(
            LocalDateTime.of(currentYear, 1, 1, 0, 0, 0),
            LocalDateTime.of(currentYear + 1, 1, 1, 0, 0, 0)
        );
    }

    private long calculateCounselorCountThisYear() {
        int currentYear = LocalDateTime.now().getYear();
        Long count = counselSessionRepository.countDistinctCounselorsByCompletedSessionsInYear(currentYear);
        return count != null ? count : 0L;
    }

    private long calculateCounselHoursForThisMonth() {
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        List<CounselSession> completedSessions = counselSessionRepository
            .findCompletedSessionsByYearAndMonth(year, month);

        return completedSessions.stream()
            .mapToLong(session -> {
                Duration duration = Duration.between(
                    session.getStartDateTime(),
                    session.getEndDateTime());
                return (long) (duration.toMinutes() / 60.0);
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
            .findByCounseleeNameAndCounselorNameAndScheduledDateTimeAndStatus(
                req.pageReq(),
                req.counseleeNameKeyword(),
                req.counselorNames(),
                req.scheduledDates(),
                req.statuses()
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
