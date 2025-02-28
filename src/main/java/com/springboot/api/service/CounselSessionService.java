package com.springboot.api.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.dto.CommonCursorRes;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselsession.AddCounselSessionReq;
import com.springboot.api.dto.counselsession.AddCounselSessionRes;
import com.springboot.api.dto.counselsession.CounselSessionStatRes;
import com.springboot.api.dto.counselsession.DeleteCounselSessionReq;
import com.springboot.api.dto.counselsession.DeleteCounselSessionRes;
import com.springboot.api.dto.counselsession.SearchCounselSessionReq;
import com.springboot.api.dto.counselsession.SelectCounselSessionListByBaseDateAndCursorAndSizeReq;
import com.springboot.api.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.dto.counselsession.SelectCounselSessionPageRes;
import com.springboot.api.dto.counselsession.SelectCounselSessionRes;
import com.springboot.api.dto.counselsession.SelectPreviousCounselSessionListRes;
import com.springboot.api.dto.counselsession.UpdateCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateCounselSessionRes;
import com.springboot.api.dto.counselsession.UpdateCounselorInCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateCounselorInCounselSessionRes;
import com.springboot.api.dto.counselsession.UpdateStatusInCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateStatusInCounselSessionRes;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.CounseleeRepository;
import com.springboot.api.repository.CounselorRepository;
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
        public AddCounselSessionRes addCounselSession(AddCounselSessionReq addCounselSessionReq) {
                LocalDateTime scheduledStartDateTime = dateTimeUtil
                                .parseToDateTime(addCounselSessionReq.getScheduledStartDateTime());

                Counselor counselor = findAndValidateCounselorSchedule(addCounselSessionReq.getCounselorId(),
                                scheduledStartDateTime);
                Counselee counselee = findAndValidateCounseleeSchedule(addCounselSessionReq.getCounseleeId(),
                                scheduledStartDateTime);

                CounselSession counselSession = CounselSession.builder()
                                .counselor(counselor)
                                .counselee(counselee)
                                .scheduledStartDateTime(scheduledStartDateTime)
                                .status(addCounselSessionReq.getStatus())
                                .build();

                CounselSession savedCounselSession = counselSessionRepository.save(counselSession);

                return new AddCounselSessionRes(savedCounselSession.getId());
        }

        @CacheEvict(value = { "sessionDates", "sessionStats", "sessionList" }, allEntries = true)
        @Transactional
        public UpdateCounselSessionRes updateCounselSession(UpdateCounselSessionReq updateCounselSessionReq) {
                CounselSession counselSession = counselSessionRepository
                                .findById(updateCounselSessionReq.getCounselSessionId()).orElseThrow(
                                                NoContentException::new);

                LocalDateTime scheduledStartDateTime = dateTimeUtil
                                .parseToDateTime(updateCounselSessionReq.getScheduledStartDateTime());

                Counselor counselor = findAndValidateCounselorSchedule(updateCounselSessionReq.getCounselorId(),
                                scheduledStartDateTime);
                Counselee counselee = findAndValidateCounseleeSchedule(updateCounselSessionReq.getCounseleeId(),
                                scheduledStartDateTime);

                counselSession.setScheduledStartDateTime(scheduledStartDateTime);
                counselSession.setCounselor(counselor);
                counselSession.setCounselee(counselee);

                return new UpdateCounselSessionRes(updateCounselSessionReq.getCounselSessionId());
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
                                        JsonNode baseInfo = session.getCounselCard().getBaseInformation()
                                                        .get("baseInfo");
                                        return new SelectPreviousCounselSessionListRes(
                                                        session.getId(),
                                                        baseInfo.get("counselSessionOrder").asText(),
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

                var page = counselSessionRepository.findByCounseleeNameAndCounselorNameAndScheduledDateTime(
                                req.getCounseleeNameKeyword(),
                                req.getCounselorName(),
                                req.getScheduledDate(),
                                pageable);

                List<SelectCounselSessionListItem> content = page.getContent().stream()
                                .map(SelectCounselSessionListItem::from)
                                .toList();

                return SelectCounselSessionPageRes.builder()
                                .content(content)
                                .totalPages(page.getTotalPages())
                                .totalElements(page.getTotalElements())
                                .numberOfElements(page.getNumberOfElements())
                                .number(page.getNumber())
                                .size(page.getSize())
                                .first(page.isFirst())
                                .last(page.isLast())
                                .build();
        }
}
