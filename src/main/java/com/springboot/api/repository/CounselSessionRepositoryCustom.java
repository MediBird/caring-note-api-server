package com.springboot.api.repository;

import java.time.LocalDate;
import java.util.List;

import com.springboot.api.domain.CounselSession;
import com.springboot.enums.ScheduleStatus;

public interface CounselSessionRepositoryCustom {
    List<LocalDate> findDistinctDatesByYearAndMonth(int year, int month);

    List<CounselSession> findCompletedSessionsByYearAndMonth(int year, int month);

    long countByStatus(ScheduleStatus status);

    long countDistinctCounseleeForCurrentMonth();
}
