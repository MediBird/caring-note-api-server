package com.springboot.api.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.api.domain.CounselSession;
import com.springboot.enums.ScheduleStatus;

public interface CounselSessionRepositoryCustom {
        List<LocalDate> findDistinctDatesByYearAndMonth(int year, int month);

        List<CounselSession> findCompletedSessionsByYearAndMonth(int year, int month);

        List<CounselSession> findSessionByCursorAndDate(LocalDate date, String cursorId, String counselorId,
                        Pageable pageable);

        long countByStatus(ScheduleStatus status);

        long countDistinctCounseleeForCurrentMonth();

        long cancelOverDueSessions();

        List<CounselSession> findPreviousCompletedSessionsOrderByEndDateTimeDesc(String counseleeId,
                        LocalDateTime beforeDateTime);

        Page<CounselSession> findByCounseleeNameAndCounselorNameAndScheduledDateTime(
                        String counseleeNameKeyword,
                        String counselorName,
                        LocalDate scheduledDate,
                        Pageable pageable);
}
