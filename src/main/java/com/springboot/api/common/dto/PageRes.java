package com.springboot.api.common.dto;

import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageRes<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PageRes(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }

    private PageRes(List<T> content, PageRes<?> pageRes) {
        this.content = content;
        this.page = pageRes.getPage();
        this.size = pageRes.getSize();
        this.totalElements = pageRes.getTotalElements();
        this.totalPages = pageRes.getTotalPages();
        this.hasNext = pageRes.isHasNext();
        this.hasPrevious = pageRes.isHasPrevious();
    }

    public <D> PageRes<D> map(Function<T, D> mapper) {
        List<D> content = this.content.stream()
            .map(mapper)
            .toList();

        return new PageRes<>(content, this);
    }
}
