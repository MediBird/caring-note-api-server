package com.springboot.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
@Getter
public class PageReq {

    private final int page;

    private final int size;

    public static PageReq of(int page, int size) {
        return new PageReq(page, size);
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}
