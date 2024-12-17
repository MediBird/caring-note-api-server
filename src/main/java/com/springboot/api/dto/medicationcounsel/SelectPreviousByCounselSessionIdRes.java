package com.springboot.api.dto.medicationcounsel;

import java.util.List;

public record SelectPreviousByCounselSessionIdRes(
        String previousCounselSessionId
        , List<String> counselRecordHighlights
        , String counselNoteSummary
) {
}
