package com.springboot.api.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.BaseEntity;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselsession.AddCounselSessionReq;
import com.springboot.api.dto.counselsession.AddCounselSessionRes;
import com.springboot.api.dto.counselsession.CounselSessionStatRes;
import com.springboot.api.dto.counselsession.DeleteCounselSessionReq;
import com.springboot.api.dto.counselsession.DeleteCounselSessionRes;
import com.springboot.api.dto.counselsession.SelectCounselSessionListByBaseDateAndCursorAndSizeReq;
import com.springboot.api.dto.counselsession.SelectCounselSessionListByBaseDateAndCursorAndSizeRes;
import com.springboot.api.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.dto.counselsession.SelectCounselSessionRes;
import com.springboot.api.dto.counselsession.SelectPreviousCounselSessionListRes;
import com.springboot.api.dto.counselsession.UpdateCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateCounselSessionRes;
import com.springboot.api.dto.counselsession.UpdateCounselorInCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateCounselorInCounselSessionRes;
import com.springboot.api.dto.counselsession.UpdateStatusInCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateStatusInCounselSessionRes;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.CounselorRepository;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.ScheduleStatus;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounselSessionService {

        private final EntityManager entityManager;
        private final DateTimeUtil dateTimeUtil;
        private final CounselSessionRepository counselSessionRepository;
        private final CounselorRepository counselorRepository;

        @CacheEvict(value = { "sessionDates", "sessionStats", "sessionList" }, allEntries = true)
        @Transactional
        public AddCounselSessionRes addCounselSession(AddCounselSessionReq addCounselSessionReq) {
                Counselor proxyCounselor = entityManager.getReference(Counselor.class,
                                addCounselSessionReq.getCounselorId());
                Counselee proxyCounselee = entityManager.getReference(Counselee.class,
                                addCounselSessionReq.getCounseleeId());

                CounselSession counselSession = CounselSession.builder()
                                .counselor(proxyCounselor)
                                .counselee(proxyCounselee)
                                .scheduledStartDateTime(dateTimeUtil
                                                .parseToDateTime(addCounselSessionReq.getScheduledStartDateTime()))
                                .status(addCounselSessionReq.getStatus())
                                .build();

                CounselSession savedCounselSession = counselSessionRepository.save(counselSession);

                return new AddCounselSessionRes(savedCounselSession.getId());
        }

        public SelectCounselSessionRes selectCounselSession(String id) {
                CounselSession counselSession = counselSessionRepository.findById(id).orElseThrow(
                                IllegalArgumentException::new);

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

        @Cacheable(value = "sessionList", key = "#req.baseDate + '-' + #req.cursor + '-' + #req.size")
        @Transactional
        public SelectCounselSessionListByBaseDateAndCursorAndSizeRes selectCounselSessionListByBaseDateAndCursorAndSize(
                        SelectCounselSessionListByBaseDateAndCursorAndSizeReq req) {
                Pageable pageable = PageRequest.of(0, req.getSize());
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                List<CounselSession> sessions;

                LocalDateTime startOfDay = Optional
                                .ofNullable(req.getBaseDate())
                                .map(LocalDate::atStartOfDay)
                                .orElse(null);

                LocalDateTime endOfDay = Optional
                                .ofNullable(req.getBaseDate())
                                .map(d -> d.plusDays(1))
                                .map(LocalDate::atStartOfDay)
                                .orElse(null);

                if (req.getBaseDate() == null) {
                        sessions = counselSessionRepository.findByCursor(
                                        req.getCursor(),
                                        null,
                                        pageable);
                } else {
                        sessions = counselSessionRepository.findByDateAndCursor(
                                        req.getBaseDate()
                                                        .atStartOfDay(),
                                        Optional.of(req.getBaseDate()
                                                        .atStartOfDay()).map(d -> d.plusDays(1)).get(),
                                        req.getCursor(),
                                        null,
                                        pageable);
                }

                String nextCursorId = null;

                if (!sessions.isEmpty()) {
                        CounselSession lastSession = sessions.getLast();
                        nextCursorId = lastSession.getId();
                }

                // hasNext 계산
                boolean hasNext = sessions.size() == req.getSize();

                List<SelectCounselSessionListItem> sessionListItems = sessions.stream()
                                .map(s -> SelectCounselSessionListItem.builder()
                                                .counseleeId(Optional.ofNullable(s.getCounselee())
                                                                .map(BaseEntity::getId)
                                                                .orElse(""))
                                                .counselorName(Optional.ofNullable(s.getCounselor())
                                                                .map(Counselor::getName)
                                                                .orElse(""))
                                                .counselorId(Optional.ofNullable(s.getCounselor())
                                                                .map(Counselor::getId)
                                                                .orElse(""))
                                                .counseleeName(Optional.ofNullable(s.getCounselee())
                                                                .map(Counselee::getName)
                                                                .orElse(""))
                                                .scheduledDate(s.getScheduledStartDateTime().toLocalDate().toString())
                                                .scheduledTime(s.getScheduledStartDateTime().toLocalTime()
                                                                .format(timeFormatter))
                                                .counselSessionId(s.getId())
                                                .isCounselorAssign(Optional.ofNullable(s.getCounselor()).isPresent())
                                                .status(s.getStatus())
                                                .cardRecordStatus(Optional.ofNullable(s.getCounselCard())
                                                                .map(CounselCard::getCardRecordStatus)
                                                                .orElse(CardRecordStatus.UNRECORDED))
                                                .build())
                                .toList();

                if (sessionListItems.isEmpty()) {
                        throw new NoContentException();
                }

                return new SelectCounselSessionListByBaseDateAndCursorAndSizeRes(sessionListItems, nextCursorId,
                                hasNext);
        }

        @CacheEvict(value = { "sessionDates", "sessionStats", "sessionList" }, allEntries = true)
        @Transactional
        public UpdateCounselSessionRes updateCounselSession(UpdateCounselSessionReq updateCounselSessionReq) {
                CounselSession counselSession = counselSessionRepository
                                .findById(updateCounselSessionReq.getCounselSessionId()).orElseThrow(
                                                NoContentException::new);

                Counselor proxyCounselor = entityManager.getReference(Counselor.class,
                                updateCounselSessionReq.getCounselorId());
                Counselee proxyCounselee = entityManager.getReference(Counselee.class,
                                updateCounselSessionReq.getCounseleeId());
                counselSession.setScheduledStartDateTime(dateTimeUtil
                                .parseToDateTime(updateCounselSessionReq.getScheduledStartDateTime()));
                counselSession.setCounselor(proxyCounselor);
                counselSession.setCounselee(proxyCounselee);

                return new UpdateCounselSessionRes(updateCounselSessionReq.getCounselSessionId());
        }

        @CacheEvict(value = { "sessionList" }, allEntries = true)
        @Transactional
        public UpdateCounselorInCounselSessionRes updateCounselorInCounselSession(
                        UpdateCounselorInCounselSessionReq updateCounselorInCounselSessionReq) {
                CounselSession counselSession = counselSessionRepository
                                .findById(updateCounselorInCounselSessionReq.counselSessionId())
                                .orElseThrow(NoContentException::new);

                String counselorId = updateCounselorInCounselSessionReq.counselorId();
                Counselor counselor = counselorRepository.findById(counselorId)
                                .orElseThrow(NoContentException::new);

                counselSession.setCounselor(counselor);

                return new UpdateCounselorInCounselSessionRes(counselSession.getId());
        }

        @CacheEvict(value = { "sessionStats", "sessionList" }, allEntries = true)
        @Transactional
        public UpdateStatusInCounselSessionRes updateStatusInCounselSession(
                        UpdateStatusInCounselSessionReq updateStatusInCounselSessionReq) {

                CounselSession counselSession = counselSessionRepository
                                .findById(updateStatusInCounselSessionReq.counselSessionId())
                                .orElseThrow(NoContentException::new);

                counselSession.setStatus(updateStatusInCounselSessionReq.status());
                if (ScheduleStatus.COMPLETED.equals(updateStatusInCounselSessionReq.status())) {
                        Counselee counselee = counselSession.getCounselee();
                        if (counselee != null) {
                                // 완료된 상담 세션 수 계산
                                long completedCount = counselee.getCounselSessions().stream()
                                                .filter(session -> ScheduleStatus.COMPLETED.equals(session.getStatus()))
                                                .count();

                                counselee.setCounselCount((int) completedCount);
                                counselee.setLastCounselDate(counselSession.getScheduledStartDateTime().toLocalDate());
                        }
                }

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
                                .findByCounseleeIdAndScheduledStartDateTimeLessThan(counselee.getId(),
                                                counselSession.getScheduledStartDateTime());

                List<SelectPreviousCounselSessionListRes> selectPreviousCounselSessionListResList = previousCounselSessions
                                .stream()
                                .filter(session -> ScheduleStatus.COMPLETED.equals(session.getStatus()))
                                .sorted(Comparator.comparing(CounselSession::getEndDateTime).reversed())
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
                // 입력값 유효성 검사
                if (month < 1 || month > 12) {
                        throw new IllegalArgumentException("월은 1-12 사이여야 합니다");
                }
                if (year < 1900 || year > 9999) {
                        throw new IllegalArgumentException("연도가 유효하지 않습니다");
                }

                try {
                        return counselSessionRepository.findDistinctDatesByYearAndMonth(year, month);
                } catch (Exception e) {
                        throw new RuntimeException("상담 일정 조회 중 오류가 발생했습니다", e);
                }
        }

        @Cacheable(value = "sessionStats")
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
                LocalDateTime twentyFourHoursAgo = now.minusHours(24);

                List<CounselSession> overdueSessions = counselSessionRepository
                                .findByStatusAndScheduledStartDateTimeBefore(
                                                ScheduleStatus.SCHEDULED,
                                                twentyFourHoursAgo);

                overdueSessions.forEach(session -> {
                        session.setStatus(ScheduleStatus.CANCELED);
                        counselSessionRepository.save(session);
                });
        }

}
