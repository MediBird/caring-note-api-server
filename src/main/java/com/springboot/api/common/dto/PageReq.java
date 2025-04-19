package com.springboot.api.common.dto;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class PageReq {

    private final int page;

    private final int size;

    public PageReq(int page, int size) {
        this.page = Math.max(page, 0);
        this.size = Math.max(size, 10);
    }

    public static PageReq of(int page, int size) {
        return new PageReq(page, size);
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}
