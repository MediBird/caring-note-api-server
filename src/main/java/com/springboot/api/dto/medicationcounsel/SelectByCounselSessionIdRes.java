package com.springboot.api.dto.medicationcounsel;

import com.springboot.enums.CounselNeedStatus;

import java.util.List;

public record SelectByCounselSessionIdRes(
        String medicationCounselId
        ,String counselRecord
        ,List<String>counselRecordHighlights
        ,CounselNeedStatus counselNeedStatus
){}
