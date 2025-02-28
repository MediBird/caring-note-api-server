package com.springboot.api.dto.counselsession;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectCounselSessionPageRes {
    private List<SelectCounselSessionListItem> content;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private int number;
    private int size;
    private boolean first;
    private boolean last;
}