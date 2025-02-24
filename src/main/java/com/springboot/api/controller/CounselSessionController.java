package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonCursorRes;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counselsession.*;
import com.springboot.api.service.CounselSessionService;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@ApiController(name = "CounselSessionController", description = "상담 세션 관련 API를 제공하는 Controller", path = "/v1/counsel/session")
@RequiredArgsConstructor
public class CounselSessionController {

    private final CounselSessionService counselSessionService;

    @Operation(summary = "상담세션(일정) 추가", tags = {"관리자 화면"})
    @PostMapping
    @RoleSecured(RoleType.ROLE_ADMIN)
    public ResponseEntity<CommonRes<AddCounselSessionRes>> addCounselSession(
            @RequestBody @Valid AddCounselSessionReq addCounselSessionReq) {

        AddCounselSessionRes addCounselSessionRes = counselSessionService
                .addCounselSession(addCounselSessionReq);

        CommonRes<AddCounselSessionRes> commonRes = new CommonRes<>(addCounselSessionRes);
        return ResponseEntity.ok(commonRes);

    }

    @GetMapping("/sessions/dates")
    @Operation(summary = "특정 연월의 상담 세션이 있는 날짜 목록 조회")
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<List<LocalDate>>> getSessionDatesByYearAndMonth(
            @RequestParam @Min(value = 1900, message = "연도는 1900년 이상이어야 합니다")
            @Max(value = 9999, message = "연도는 9999년 이하여야 합니다") int year,
            @RequestParam @Min(value = 1, message = "월은 1 이상이어야 합니다")
            @Max(value = 12, message = "월은 12 이하여야 합니다") int month) {
        List<LocalDate> dates = counselSessionService.getSessionDatesByYearAndMonth(year, month);
        return ResponseEntity.ok(new CommonRes<>(dates));
    }

    @GetMapping("/sessions/stats")
    @Operation(summary = "상담 세션 통계 조회")
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER, RoleType.ROLE_ASSISTANT})
    public ResponseEntity<CommonRes<CounselSessionStatRes>> getSessionStats() {
        CounselSessionStatRes stats = counselSessionService.getSessionStats();
        return ResponseEntity.ok(new CommonRes<>(stats));
    }

    @GetMapping("/list")
    @Operation(summary = "상담일정 목록 조회", tags = {"로그인/홈"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonCursorRes<List<SelectCounselSessionListItem>>> selectCounselSessionListByBaseDateAndCursorAndSize(
            @RequestParam(required = false) LocalDate baseDate,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "15") int size) {

        SelectCounselSessionListByBaseDateAndCursorAndSizeReq selectCounselSessionListByBaseDateAndCursorAndSizeReq = SelectCounselSessionListByBaseDateAndCursorAndSizeReq
                .builder()
                .baseDate(baseDate)
                .cursor(cursor)
                .size(size)
                .build();

        SelectCounselSessionListByBaseDateAndCursorAndSizeRes selectCounselSessionListByBaseDateAndCursorAndSizeRes = counselSessionService
                .selectCounselSessionListByBaseDateAndCursorAndSize(
                        selectCounselSessionListByBaseDateAndCursorAndSizeReq);

        return ResponseEntity.ok(new CommonCursorRes<>(
                selectCounselSessionListByBaseDateAndCursorAndSizeRes.sessionListItems(),
                selectCounselSessionListByBaseDateAndCursorAndSizeRes.nextCursor(),
                selectCounselSessionListByBaseDateAndCursorAndSizeRes.hasNext()));
    }

    @GetMapping("/{counselSessionId}")
    @Operation(summary = "상담일정 조회", tags = {"관리자 화면"})
    public ResponseEntity<CommonRes<SelectCounselSessionRes>> selectCounselSession(
            @PathVariable String counselSessionId) {

        SelectCounselSessionRes selectCounselSessionRes = counselSessionService
                .selectCounselSession(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectCounselSessionRes));

    }

    @PutMapping
    @Operation(summary = "상담일정 수정", tags = {"관리자 화면"})
    @RoleSecured(RoleType.ROLE_ADMIN)
    public ResponseEntity<CommonRes<UpdateCounselSessionRes>> updateCounselSession(
            @RequestBody @Valid UpdateCounselSessionReq updateCounselSessionReq) {

        UpdateCounselSessionRes updateCounselSessionRes = counselSessionService
                .updateCounselSession(updateCounselSessionReq);

        return ResponseEntity.ok(new CommonRes<>(updateCounselSessionRes));
    }

    @PutMapping("/counselor")
    @Operation(summary = "상담일정 담당 약사 수정", tags = {
            "로그인/홈"}, description = "req body에 counselorId 넣지 않은 경우 로그인 계정 정보로 할당됨. ")
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<UpdateCounselorInCounselSessionRes>> updateCounselorInCounselSession(
            @RequestBody @Valid UpdateCounselorInCounselSessionReq updateCounselorInCounselSessionReq) {

        UpdateCounselorInCounselSessionRes updateCounselorInCounselSessionRes = counselSessionService
                .updateCounselorInCounselSession(
                        updateCounselorInCounselSessionReq);

        return ResponseEntity.ok(new CommonRes<>(updateCounselorInCounselSessionRes));

    }

    @PutMapping("/status")
    @Operation(summary = "상담 상태 수정", tags = {"로그인/홈"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<UpdateStatusInCounselSessionRes>> updateStatusInCounselSession(
            @RequestBody @Valid UpdateStatusInCounselSessionReq updateStatusInCounselSessionReq) {
        UpdateStatusInCounselSessionRes updateStatusInCounselSessionRes = counselSessionService
                .updateStatusInCounselSession(updateStatusInCounselSessionReq);

        return ResponseEntity.ok(new CommonRes<>(updateStatusInCounselSessionRes));

    }

    @DeleteMapping
    @Operation(summary = "상담일정 삭제", tags = {"관리자 화면"})
    @RoleSecured(RoleType.ROLE_ADMIN)
    public ResponseEntity<CommonRes<DeleteCounselSessionRes>> deleteCounselSession(
            @RequestBody @Valid DeleteCounselSessionReq deleteCounselSessionReq) {

        DeleteCounselSessionRes deleteCounselSessionRes = counselSessionService
                .deleteCounselSessionRes(deleteCounselSessionReq);
        return ResponseEntity.ok(new CommonRes<>(deleteCounselSessionRes));

    }

    @GetMapping("/{counselSessionId}/previous/list")
    @Operation(summary = "이전 상담 내역 조회", tags = {"본상담 - 이전 상담 내역"})
    @RoleSecured(RoleType.ROLE_ADMIN)
    public ResponseEntity<CommonRes<List<SelectPreviousCounselSessionListRes>>> selectPreviousCounselSessionList(
            @PathVariable("counselSessionId") String counselSessionId) {

        List<SelectPreviousCounselSessionListRes> selectPreviousListByCounselSessionIdResList = counselSessionService
                .selectPreviousCounselSessionList(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectPreviousListByCounselSessionIdResList));

    }

}
