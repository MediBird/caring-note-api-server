package com.springboot.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counselee.AddAndUpdateCounseleeReq;
import com.springboot.api.dto.counselee.SelectCounseleeBaseInformationByCounseleeIdRes;
import com.springboot.api.dto.counselee.SelectCounseleeRes;
import com.springboot.api.service.CounseleeService;
import com.springboot.enums.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@ApiController(name = "CounseleeController", description = "내담자 관련 API를 제공하는 Controller", path = "/v1/counsel/counselee")
@RequiredArgsConstructor
public class CounseleeController {

        private final CounseleeService counseleeService;

        @GetMapping("/{counselSessionId}/base/information")
        @Operation(summary = "내담자 기본 정보 조회", tags = { "상담 카드 작성", "상담 노트" })
        public ResponseEntity<CommonRes<SelectCounseleeBaseInformationByCounseleeIdRes>> selectCounseleeBaseInformation(
                        @PathVariable("counselSessionId") String counselSessionId) {

                SelectCounseleeBaseInformationByCounseleeIdRes selectCounseleeBaseInformationByCounseleeIdRes = counseleeService
                                .selectCounseleeBaseInformation(counselSessionId);

                return ResponseEntity.ok(new CommonRes<>(selectCounseleeBaseInformationByCounseleeIdRes));
        }

        @PostMapping("/")
        @Operation(summary = "내담자 기본 정보 생성", tags = { "상담 카드 작성", "상담 노트" })
        public ResponseEntity<CommonRes<String>> addAndUpdateCounselee(
                        @RequestBody AddAndUpdateCounseleeReq addAndUpdateCounseleeReq) {

                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.addAndUpdateCounselee(addAndUpdateCounseleeReq)));
        }

        @GetMapping("/")
        @Operation(summary = "내담자 기본 정보 생성", tags = { "상담 카드 작성", "상담 노트" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<SelectCounseleeRes>> selectCounselees(
                        @RequestParam("counseleeId") String counseleeId) {
                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.selectCounselees(counseleeId)));
        }

}
