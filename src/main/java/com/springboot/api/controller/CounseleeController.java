package com.springboot.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counselee.AddCounseleeReq;
import com.springboot.api.dto.counselee.SelectCounseleeBaseInformationByCounseleeIdRes;
import com.springboot.api.dto.counselee.SelectCounseleeRes;
import com.springboot.api.dto.counselee.UpdateCounseleeReq;
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
        @Operation(summary = "내담자 기본 정보 생성", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<String>> addCounselee(
                        @RequestBody AddCounseleeReq addCounseleeReq) {

                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.addCounselee(addCounseleeReq)));
        }

        @PutMapping("/")
        @Operation(summary = "내담자 기본 정보 수정", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<String>> updateCounselee(
                        @RequestBody UpdateCounseleeReq updateCounseleeReq) {
                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.updateCounselee(updateCounseleeReq)));
        }

        @GetMapping("/{counseleeId}")
        @Operation(summary = "내담자 상세 정보 조회", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<SelectCounseleeRes>> selectCounselee(
                        @PathVariable("counseleeId") String counseleeId) {
                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.selectCounselee(counseleeId)));
        }

        @GetMapping("/")
        @Operation(summary = "내담자 목록 조회", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<List<SelectCounseleeRes>>> selectCounselees(
                        @RequestParam("page") int page,
                        @RequestParam("size") int size) {
                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.selectCounselees(page, size)));
        }

        @DeleteMapping("/{counseleeId}")
        @Operation(summary = "내담자 삭제", tags = { "내담자 관리" })
        // @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<String>> deleteCounselee(
                        @PathVariable("counseleeId") String counseleeId) {
                counseleeService.deleteCounselee(counseleeId);
                return ResponseEntity.ok(new CommonRes<>("delete the counselee success"));
        }
}
