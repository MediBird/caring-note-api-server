package com.springboot.api.counselsession.repository;

import com.querydsl.core.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.ScheduleStatus;

public interface CounselSessionRepositoryCustom {
        List<LocalDate> findDistinctDatesByYearAndMonth(int year, int month);

        List<CounselSession> findCompletedSessionsByYearAndMonth(int year, int month);

        List<Tuple> findSessionByCursorAndDate(LocalDate date, String cursorId, String counselorId,
                        Pageable pageable);

        long countByStatus(ScheduleStatus status);

        long countDistinctCounseleeForCurrentMonth();

        long cancelOverDueSessions();

        List<CounselSession> findPreviousCompletedSessionsOrderByEndDateTimeDesc(String counseleeId,
                        LocalDateTime beforeDateTime);

        Page<CounselSession> findByCounseleeNameAndCounselorNameAndScheduledDateTime(
                        String counseleeNameKeyword,
                        List<String> counselorNames,
                        List<LocalDate> scheduledDates,
                        Pageable pageable);

        List<String> findAllPharmacistNames();

        Optional<CounselSession> findByIdWithCounseleeAndCounselor(String counselSessionId);

        Optional<CounselSession> findByIdWithCounselee(String counselSessionId);

        /**
         * 특정 내담자의 상담 회차 수를 계산합니다.
         * 
         * @param counseleeId    내담자 ID
         * @param beforeDateTime 이 시간 이전의 상담 세션만 계산 (null인 경우 모든 세션 계산)
         * @return 상담 회차 수
         */
        int countSessionNumberByCounseleeId(String counseleeId, LocalDateTime beforeDateTime);
}
