package com.springboot.api.counselsession.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonCursorRes;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.counselsession.dto.counselsession.AddCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.AddCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.CounselSessionStatRes;
import com.springboot.api.counselsession.dto.counselsession.DeleteCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.DeleteCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.SearchCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionListByBaseDateAndCursorAndSizeReq;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionPageRes;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.SelectPreviousCounselSessionListRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselorInCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselorInCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateStatusInCounselSessionReq;
import com.springboot.api.counselsession.dto.counselsession.UpdateStatusInCounselSessionRes;
import com.springboot.api.counselsession.service.CounselSessionService;
import com.springboot.enums.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@ApiController(name = "CounselSessionController", description = "상담 세션 관련 API를 제공하는 Controller", path = "/v1/counsel/session")
@RequiredArgsConstructor
public class CounselSessionController {

        private final CounselSessionService counselSessionService;

        @Operation(summary = "상담세션(일정) 추가", tags = { "관리자 화면" })
        @PostMapping
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<AddCounselSessionRes>> addCounselSession(
                        @RequestBody @Valid AddCounselSessionReq addCounselSessionReq) {
                AddCounselSessionRes addCounselSessionRes = counselSessionService
                                .addCounselSession(addCounselSessionReq);
                return ResponseEntity.ok(new CommonRes<>(addCounselSessionRes));
        }

        @Operation(summary = "특정 연월의 상담 세션이 있는 날짜 목록 조회")
        @GetMapping("/sessions/dates")
        @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
        public ResponseEntity<CommonRes<List<LocalDate>>> getSessionDatesByYearAndMonth(
                        @RequestParam @Min(value = 1900, message = "연도는 1900년 이상이어야 합니다") @Max(value = 9999, message = "연도는 9999년 이하여야 합니다") int year,
                        @RequestParam @Min(value = 1, message = "월은 1 이상이어야 합니다") @Max(value = 12, message = "월은 12 이하여야 합니다") int month) {
                List<LocalDate> dates = counselSessionService.getSessionDatesByYearAndMonth(year, month);
                return ResponseEntity.ok(new CommonRes<>(dates));
        }

        @Operation(summary = "상담 세션 통계 조회")
        @GetMapping("/sessions/stats")
        @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER, RoleType.ROLE_ASSISTANT })
        public ResponseEntity<CommonRes<CounselSessionStatRes>> getSessionStats() {
                CounselSessionStatRes stats = counselSessionService.getSessionStats();
                return ResponseEntity.ok(new CommonRes<>(stats));
        }

        @Operation(summary = "상담일정 목록 조회", tags = { "로그인/홈" })
        @GetMapping("/list")
        @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
        public ResponseEntity<CommonCursorRes<List<SelectCounselSessionListItem>>> selectCounselSessionListByBaseDateAndCursorAndSize(
                        @RequestParam(required = false) LocalDate baseDate,
                        @RequestParam(required = false) String cursor,
                        @RequestParam(defaultValue = "15") int size) {
                SelectCounselSessionListByBaseDateAndCursorAndSizeReq req = SelectCounselSessionListByBaseDateAndCursorAndSizeReq
                                .builder()
                                .baseDate(baseDate)
                                .cursor(cursor)
                                .size(size)
                                .build();

                return ResponseEntity.ok(counselSessionService.selectCounselSessionListByBaseDateAndCursorAndSize(req));
        }

        @Operation(summary = "상담일정 조회", tags = { "관리자 화면" })
        @GetMapping("/{counselSessionId}")
        public ResponseEntity<CommonRes<SelectCounselSessionRes>> selectCounselSession(
                        @PathVariable String counselSessionId) {
                SelectCounselSessionRes selectCounselSessionRes = counselSessionService
                                .selectCounselSession(counselSessionId);
                return ResponseEntity.ok(new CommonRes<>(selectCounselSessionRes));
        }

        @Operation(summary = "상담일정 검색 및 필터링", tags = { "관리자 화면" })
        @GetMapping("/")
        @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
        public ResponseEntity<CommonRes<SelectCounselSessionPageRes>> searchCounselSessions(
                        @RequestParam("page") @Min(0) int page,
                        @RequestParam("size") @Min(1) @Max(100) int size,
                        @RequestParam(required = false, name = "counseleeNameKeyword") @Pattern(regexp = "^[가-힣a-zA-Z\\s]*$", message = "이름은 한글과 영문만 허용됩니다") String counseleeNameKeyword,
                        @RequestParam(required = false, name = "counselorNames") List<String> counselorNames,
                        @RequestParam(required = false) List<LocalDate> scheduledDates) {

                SearchCounselSessionReq searchCounselSessionReq = SearchCounselSessionReq.builder()
                                .page(page)
                                .size(size)
                                .counseleeNameKeyword(counseleeNameKeyword)
                                .counselorNames(counselorNames)
                                .scheduledDates(scheduledDates)
                                .build();

                return ResponseEntity.ok(
                                new CommonRes<>(counselSessionService.searchCounselSessions(searchCounselSessionReq)));
        }

        @Operation(summary = "상담일정 수정", tags = { "관리자 화면" })
        @PutMapping
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<UpdateCounselSessionRes>> updateCounselSession(
                        @RequestBody @Valid UpdateCounselSessionReq updateCounselSessionReq) {
                UpdateCounselSessionRes updateCounselSessionRes = counselSessionService
                                .updateCounselSession(updateCounselSessionReq);
                return ResponseEntity.ok(new CommonRes<>(updateCounselSessionRes));
        }

        @Operation(summary = "상담일정 담당 약사 수정", tags = {
                        "로그인/홈" }, description = "req body에 counselorId 넣지 않은 경우 로그인 계정 정보로 할당됨.")
        @PutMapping("/counselor")
        @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
        public ResponseEntity<CommonRes<UpdateCounselorInCounselSessionRes>> updateCounselorInCounselSession(
                        @RequestBody @Valid UpdateCounselorInCounselSessionReq updateCounselorInCounselSessionReq) {
                UpdateCounselorInCounselSessionRes result = counselSessionService
                                .updateCounselorInCounselSession(updateCounselorInCounselSessionReq);
                return ResponseEntity.ok(new CommonRes<>(result));
        }

        @Operation(summary = "상담 상태 수정", tags = { "로그인/홈" })
        @PutMapping("/status")
        @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
        public ResponseEntity<CommonRes<UpdateStatusInCounselSessionRes>> updateStatusInCounselSession(
                        @RequestBody @Valid UpdateStatusInCounselSessionReq updateStatusInCounselSessionReq) {
                UpdateStatusInCounselSessionRes result = counselSessionService
                                .updateStatusInCounselSession(updateStatusInCounselSessionReq);
                return ResponseEntity.ok(new CommonRes<>(result));
        }

        @Operation(summary = "상담일정 삭제", tags = { "관리자 화면" })
        @DeleteMapping
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<DeleteCounselSessionRes>> deleteCounselSession(
                        @RequestBody @Valid DeleteCounselSessionReq deleteCounselSessionReq) {
                DeleteCounselSessionRes deleteCounselSessionRes = counselSessionService
                                .deleteCounselSessionRes(deleteCounselSessionReq);
                return ResponseEntity.ok(new CommonRes<>(deleteCounselSessionRes));
        }

        @Operation(summary = "이전 상담 내역 조회", tags = { "본상담 - 이전 상담 내역" })
        @GetMapping("/{counselSessionId}/previous/list")
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<List<SelectPreviousCounselSessionListRes>>> selectPreviousCounselSessionList(
                        @PathVariable("counselSessionId") String counselSessionId) {
                List<SelectPreviousCounselSessionListRes> result = counselSessionService
                                .selectPreviousCounselSessionList(counselSessionId);
                return ResponseEntity.ok(new CommonRes<>(result));
        }
}
