package com.springboot.api.counselsession.dto.counselsession;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCounselSessionReq {
    private int page;
    private int size;
    private String counseleeNameKeyword;
    private List<String> counselorNames;
    private List<LocalDate> scheduledDates;
}