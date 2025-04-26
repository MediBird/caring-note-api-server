package com.springboot.api.common.dto;

import org.springframework.data.domain.Page;

import lombok.Builder;

@Builder
public record PageRes(
    int totalPages,
    long totalElements,
    int currentPage,
    boolean hasNext,
    boolean hasPrevious) {

    public static <T> PageRes from(Page<T> page) {
        return PageRes.builder()
            .totalPages(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .currentPage(page.getNumber())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .build();
    }
}
