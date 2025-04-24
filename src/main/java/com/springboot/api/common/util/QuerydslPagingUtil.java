package com.springboot.api.common.util;

import com.querydsl.jpa.impl.JPAQuery;
import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class QuerydslPagingUtil {

    public static <T> PageRes<T> applyPagination(
        PageReq pageReq,
        JPAQuery<T> contentQuery,
        JPAQuery<Long> countQuery
    ) {
        Pageable pageable = pageReq.toPageable();

        List<T> content = contentQuery
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = countQuery.fetchOne();

        Page<T> page = new PageImpl<>(content, pageable, total == null ? 0 : total);
        return new PageRes<>(page);
    }
}
