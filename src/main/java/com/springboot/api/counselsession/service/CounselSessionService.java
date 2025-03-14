package com.springboot.api.counselsession.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.dto.CommonCursorRes;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselor.repository.CounselorRepository;
import com.springboot.api.counselsession.dto.counselsession.CounselSessionStatRes;
import com.springboot.api.counselsession.dto.counselsession.CreateCounselReservationReq;
import com.springboot.api.counselsession.dto.counselsession.CreateCounselReservationRes;
import com.springboot.api.counselsession.dto.counselsession.DeleteCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.DeleteCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.ModifyCounselReservationReq;
import com.springboot.api.counselsession.dto.counselsession.ModifyCounselReservationRes;
import com.springboot.api.counselsession.dto.counselsession.SearchCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionListByBaseDateAndCursorAndSizeReq;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionPageRes;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.SelectPreviousCounselSessionListRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselorInCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselorInCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateStatusInCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateStatusInCounselSessionRes;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.enums.ScheduleStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CounselSessionService {

        private final DateTimeUtil dateTimeUtil;
        private final CounselSessionRepository counselSessionRepository;
        private final CounselorRepository counselorRepository;
        private final CounseleeRepository counseleeRepository;

        @CacheEvict(value = { "sessionDates", "sessionStats", "sessionList" }, allEntries = true)
        @Transactional
        public CreateCounselReservationRes createReservation(CreateCounselReservationReq createReservationReq) {
                LocalDateTime scheduledStartDateTime = dateTimeUtil
                                .parseToDateTime(createReservationReq.getScheduledStartDateTime());

                Counselee counselee = findAndValidateCounseleeSchedule(createReservationReq.getCounseleeId(),
                                scheduledStartDateTime);

                CounselSession counselSession = CounselSession.createReservation(
                                counselee,
                                scheduledStartDateTime);
                updateSessionNumber(counselSession);
                CounselSession savedCounselSession = counselSessionRepository.save(counselSession);

                return new CreateCounselReservationRes(savedCounselSession.getId());
        }

        @CacheEvict(value = { "sessionDates", "sessionStats", "sessionList" }, allEntries = true)
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

                return new ModifyCounselReservationRes(modifyCounselReservationReq.getCounselSessionId());
        }

        private Counselor findAndValidateCounselorSchedule(String counselorId, LocalDateTime scheduledStartDateTime) {
                Counselor counselor = counselorRepository.findById(counselorId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담사 ID입니다"));

                if (counselSessionRepository.existsByCounselorAndScheduledStartDateTime(counselor,
                                scheduledStartDateTime)) {
                        throw new IllegalArgumentException("해당 시간에 이미 상담이 예약되어 있습니다");
                }

                return counselor;
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

        @Cacheable(value = "sessionList", key = "#req.baseDate + '-' + #req.cursor + '-' + #req.size")
        @Transactional
        public CommonCursorRes<List<SelectCounselSessionListItem>> selectCounselSessionListByBaseDateAndCursorAndSize(
                        SelectCounselSessionListByBaseDateAndCursorAndSizeReq req) {
                Pageable pageable = PageRequest.of(0, req.size());

                List<CounselSession> sessions = counselSessionRepository.findSessionByCursorAndDate(req.baseDate(),
                                req.cursor(), null, pageable);

                boolean hasNext = sessions.size() > pageable.getPageSize();
                List<CounselSession> content = hasNext ? sessions.subList(0, pageable.getPageSize()) : sessions;
                String nextCursor = content.isEmpty() ? null : content.getLast().getId();

                return new CommonCursorRes<>(content.stream().map(SelectCounselSessionListItem::from).toList(),
                                nextCursor,
                                hasNext);
        }

        @CacheEvict(value = { "sessionList" }, allEntries = true)
        @Transactional
        public UpdateCounselorInCounselSessionRes updateCounselorInCounselSession(
                        UpdateCounselorInCounselSessionReq updateCounselorInCounselSessionReq) {
                CounselSession counselSession = counselSessionRepository
                                .findById(updateCounselorInCounselSessionReq.counselSessionId())
                                .orElseThrow(NoContentException::new);

                Counselor counselor = findAndValidateCounselorSchedule(updateCounselorInCounselSessionReq.counselorId(),
                                counselSession.getScheduledStartDateTime());

                counselSession.updateCounselor(counselor);

                return new UpdateCounselorInCounselSessionRes(counselSession.getId());
        }

        @CacheEvict(value = { "sessionStats", "sessionList" }, allEntries = true)
        @Transactional
        public UpdateStatusInCounselSessionRes updateStatusInCounselSession(
                        UpdateStatusInCounselSessionReq updateStatusInCounselSessionReq) {

                CounselSession counselSession = counselSessionRepository
                                .findById(updateStatusInCounselSessionReq.counselSessionId())
                                .orElseThrow(NoContentException::new);

                counselSession.updateStatus(updateStatusInCounselSessionReq.status());

                return new UpdateStatusInCounselSessionRes(counselSession.getId());
        }

        @CacheEvict(value = { "sessionDates", "sessionStats", "sessionList" }, allEntries = true)
        @Transactional
        public DeleteCounselSessionRes deleteCounselSessionRes(DeleteCounselSessionReq deleteCounselSessionReq) {

                counselSessionRepository.deleteById(deleteCounselSessionReq.getCounselSessionId());

                return new DeleteCounselSessionRes(deleteCounselSessionReq.getCounselSessionId());
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
                                .map(session -> {
                                        return new SelectPreviousCounselSessionListRes(
                                                        session.getId(),
                                                        session.getSessionNumber(),
                                                        session.getScheduledStartDateTime().toLocalDate(),
                                                        session.getCounselor().getName(),
                                                        false);
                                })
                                .toList();

                if (selectPreviousCounselSessionListResList.isEmpty()) {
                        throw new NoContentException();
                }

                return selectPreviousCounselSessionListResList;

        }

        @Cacheable(value = "sessionDates", key = "#year + '-' + #month")
        public List<LocalDate> getSessionDatesByYearAndMonth(int year, int month) {
                try {
                        return counselSessionRepository.findDistinctDatesByYearAndMonth(year, month);
                } catch (Exception e) {
                        throw new RuntimeException("상담 일정 조회 중 오류가 발생했습니다", e);
                }
        }

        @Cacheable(value = "sessionStats")
        @Transactional(readOnly = true)
        public CounselSessionStatRes getSessionStats() {
                try {
                        long totalSessionCount = calculateTotalSessionCount();
                        long counseleeCount = counselSessionRepository.countDistinctCounseleeForCurrentMonth();
                        long totalCaringMessageCount = counselSessionRepository.count();
                        double counselHours = calculateCounselHoursForThisMonth();

                        return CounselSessionStatRes.builder()
                                        .totalSessionCount(totalSessionCount)
                                        .counseleeCountForThisMonth(counseleeCount)
                                        .totalCaringMessageCount(totalCaringMessageCount)
                                        .counselHoursForThisMonth((long) counselHours)
                                        .build();
                } catch (Exception e) {
                        throw new RuntimeException("통계 정보 조회 중 오류가 발생했습니다", e);
                }
        }

        private long calculateTotalSessionCount() {
                try {
                        return counselSessionRepository.countByStatus(ScheduleStatus.COMPLETED);
                } catch (Exception e) {
                        throw new RuntimeException("완료된 상담 세션 수 계산 중 오류 발생", e);
                }
        }

        private double calculateCounselHoursForThisMonth() {
                try {
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
                } catch (Exception e) {
                        throw new RuntimeException("이번 달 상담 시간 계산 중 오류 발생", e);
                }
        }

        @Scheduled(cron = "0 0 * * * *") // 매시간 실행
        @Transactional
        public void cancelOverdueSessions() {
                LocalDateTime now = LocalDateTime.now();
                log.info("Checking for overdue sessions at {}", now);
                long updateCount = counselSessionRepository.cancelOverDueSessions();
                log.info("취소된 상담 세션 수: {}", updateCount);
        }

        @Transactional(readOnly = true)
        public SelectCounselSessionPageRes searchCounselSessions(SearchCounselSessionReq req) {
                Pageable pageable = PageRequest.of(req.getPage(), req.getSize());

                Page<CounselSession> page = counselSessionRepository
                                .findByCounseleeNameAndCounselorNameAndScheduledDateTime(
                                                req.getCounseleeNameKeyword(),
                                                req.getCounselorNames(),
                                                req.getScheduledDates(),
                                                pageable);

                return SelectCounselSessionPageRes.of(page);
        }

        /**
         * 상담 세션의 회차 정보를 업데이트합니다.
         * 내담자별로 완료된 상담 세션의 수를 계산하여 회차를 설정합니다.
         * 
         * @param counselSession 회차 정보를 업데이트할 상담 세션
         */
        public void updateSessionNumber(CounselSession counselSession) {
                String counseleeId = counselSession.getCounselee().getId();
                LocalDateTime scheduledDateTime = counselSession.getScheduledStartDateTime();
                int sessionNumber = counselSessionRepository.countSessionNumberByCounseleeId(
                                counseleeId, scheduledDateTime) + 1;
                counselSession.updateSessionNumber(sessionNumber);
        }
}
