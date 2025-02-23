package com.springboot.api.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.springboot.api.domain.CounselSession;
import com.springboot.enums.ScheduleStatus;

public interface CounselSessionRepositoryCustom {
    List<LocalDate> findDistinctDatesByYearAndMonth(int year, int month);

    List<CounselSession> findCompletedSessionsByYearAndMonth(int year, int month);

    List<CounselSession> findByStatusAndScheduledStartDateTimeBefore(ScheduleStatus status, LocalDateTime dateTime);

    long countByStatus(ScheduleStatus status);
}
