package com.springboot.api.counselsession.dto.counselsession;

import com.springboot.api.common.dto.PageReq;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record SearchCounselSessionReq(PageReq pageReq, String counseleeNameKeyword, List<String> counselorNames,
                                      List<LocalDate> scheduledDates) {

}