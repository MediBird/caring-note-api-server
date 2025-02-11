package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.common.message.HttpMessages;
import com.springboot.api.dto.counselee.*;
import com.springboot.api.service.CounseleeService;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiController(name = "CounseleeController", description = "내담자 관련 API를 제공하는 Controller", path = "/v1/counsel/counselee")
@RequiredArgsConstructor
public class CounseleeController {

        private final CounseleeService counseleeService;

        @GetMapping("/{counselSessionId}/base/information")
        @Operation(summary = "내담자 기본 정보 조회", tags = { "상담 카드 작성", "상담 노트" })
        @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
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
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<String>> deleteCounselee(
                        @PathVariable("counseleeId") String counseleeId) {
                counseleeService.deleteCounselee(counseleeId);
                return ResponseEntity.ok(new CommonRes<>("delete the counselee success"));
        }

        @DeleteMapping("/batch")
        @Operation(summary = "내담자 삭제(batch)", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<List<DeleteCounseleeBatchRes>>> deleteCounseleeBatch(
                        @RequestBody @Valid List<DeleteCounseleeBatchReq> deleteCounseleeBatchReqList) {

                List<DeleteCounseleeBatchRes> deleteCounseleeBatchResList = counseleeService
                                .deleteCounseleeBatch(deleteCounseleeBatchReqList);

                return ResponseEntity.ok(new CommonRes<>(HttpMessages.SUCCESS_DELETE, deleteCounseleeBatchResList));

        }

}
