package com.springboot.api.counselsession.dto.counselsession;

import java.time.LocalDate;
import java.util.List;

import com.springboot.api.common.dto.PageReq;
import com.springboot.enums.ScheduleStatus;

public record SearchCounselSessionReq(PageReq pageReq, String counseleeNameKeyword, List<String> counselorNames,
                                      List<LocalDate> scheduledDates, List<ScheduleStatus> statuses) {

}