package com.springboot.api.counselsession.dto.counselsession;

import java.util.List;

import lombok.Builder;

@Builder
public record SelectCounselSessionPageRes(
        List<SelectCounselSessionListItem> content,
        int totalPages,
        long totalElements,
        int numberOfElements,
        int number,
        int size,
        boolean first,
        boolean last) {
}