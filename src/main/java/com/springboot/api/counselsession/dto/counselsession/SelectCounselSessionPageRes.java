package com.springboot.api.counselsession.dto.counselsession;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.springboot.api.common.dto.PageRes;
import com.springboot.api.counselsession.entity.CounselSession;

public record SelectCounselSessionPageRes(
                List<SelectCounselSessionListItem> content,
                PageRes pageInfo) {

        public static SelectCounselSessionPageRes of(Page<CounselSession> page) {
                return new SelectCounselSessionPageRes(
                                page.getContent().stream()
                                                .map(SelectCounselSessionListItem::from)
                                                .collect(Collectors.toList()),
                                PageRes.from(page));
        }
}