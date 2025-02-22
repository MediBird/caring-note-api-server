package com.springboot.api.dto.counselee;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SelectCounseleePageRes {
    private List<SelectCounseleeRes> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private boolean hasNext;
    private boolean hasPrevious;
}