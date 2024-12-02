package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counselee.SelectByCounselSessionIdRes;
import com.springboot.api.service.CounseleeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@ApiController(
        name = "CounseleeController"
        ,description = "내담자 관련 API를 제공하는 Controller"
        ,path = "/v1/counsel/counselee"
)
public class CounseleeController {

    private final CounseleeService counseleeService;

    public CounseleeController(CounseleeService counseleeService) {

        this.counseleeService = counseleeService;
    }


    @GetMapping("/{counselSessionId}")
    public ResponseEntity<CommonRes<SelectByCounselSessionIdRes>> selectByCounselSessionId(@AuthenticationPrincipal UserDetails userDetails
            , @PathVariable("counselSessionId") String counselSessionId
            , @RequestParam String counseleeId) {

        SelectByCounselSessionIdRes selectByCounselSessionIdRes = counseleeService
                .selectByCounselSessionId(userDetails.getUsername(), counselSessionId, counseleeId);

        return ResponseEntity.ok(new CommonRes<>(selectByCounselSessionIdRes));
    }


}

