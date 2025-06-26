package com.springboot.api.counselsession.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.ScheduleStatus;

public interface CounselSessionRepositoryCustom {

    List<LocalDate> findDistinctDatesByYearAndMonth(int year, int month);

    List<CounselSession> findCompletedSessionsByYearAndMonth(int year, int month);

    PageRes<SelectCounselSessionListItem> findSessionByCursorAndDate(LocalDate date, PageReq pageReq);

    Long countByStatus(ScheduleStatus status);

    Long countDistinctCounseleeForCurrentMonth();

    List<String> cancelOverDueSessionsAndReturnAffectedCounseleeIds();

    List<CounselSession> findPreviousCompletedSessionsOrderByEndDateTimeDesc(String counseleeId,
        LocalDateTime beforeDateTime);

    PageRes<CounselSession> findByCounseleeNameAndCounselorNameAndScheduledDateTimeAndStatus(
        PageReq pageReq,
        String counseleeNameKeyword,
        List<String> counselorNames,
        List<LocalDate> scheduledDates,
        List<ScheduleStatus> statuses);

    List<CounselSession> findValidCounselSessionsByCounseleeId(String counseleeId);

    void bulkUpdateCounselSessionNum(Map<String, Integer> sessionUpdates);

    Long countDistinctCounselorsByCompletedSessionsInYear(int year);
}
