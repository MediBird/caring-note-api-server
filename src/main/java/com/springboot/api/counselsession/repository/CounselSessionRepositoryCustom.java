package com.springboot.api.counselsession.repository;

import com.querydsl.core.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Map;
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

    List<String> cancelOverDueSessionsAndReturnAffectedCounseleeIds();

    List<CounselSession> findPreviousCompletedSessionsOrderByEndDateTimeDesc(String counseleeId,
        LocalDateTime beforeDateTime);

    Page<CounselSession> findByCounseleeNameAndCounselorNameAndScheduledDateTime(
        String counseleeNameKeyword,
        List<String> counselorNames,
        List<LocalDate> scheduledDates,
        Pageable pageable);

    int countSessionNumberByCounseleeId(String counseleeId, LocalDateTime beforeDateTime);

    List<CounselSession> findValidCounselSessionsByCounseleeId(String counseleeId);

    void bulkUpdateCounselSessionNum(Map<String, Integer> sessionUpdates);
}
