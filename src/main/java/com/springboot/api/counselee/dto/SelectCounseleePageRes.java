package com.springboot.api.counselee.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.springboot.api.common.dto.PageRes;
import com.springboot.api.counselee.entity.Counselee;

public record SelectCounseleePageRes(
        PageRes pageInfo,
        List<SelectCounseleeRes> content) {
    public static SelectCounseleePageRes of(Page<Counselee> page) {
        return new SelectCounseleePageRes(
                PageRes.from(page),
                page.getContent().stream()
                        .map(SelectCounseleeRes::from)
                        .collect(Collectors.toList()));
    }

}