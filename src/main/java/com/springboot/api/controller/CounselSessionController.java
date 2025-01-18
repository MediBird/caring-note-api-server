package com.springboot.api.controller;

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
import com.springboot.api.dto.counselsession.AddCounselSessionReq;
import com.springboot.api.dto.counselsession.AddCounselSessionRes;
import com.springboot.api.dto.counselsession.DeleteCounselSessionReq;
import com.springboot.api.dto.counselsession.DeleteCounselSessionRes;
import com.springboot.api.dto.counselsession.SelectCounselSessionListByBaseDateAndCursorAndSizeReq;
import com.springboot.api.dto.counselsession.SelectCounselSessionListByBaseDateAndCursorAndSizeRes;
import com.springboot.api.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.dto.counselsession.SelectCounselSessionRes;
import com.springboot.api.dto.counselsession.SelectPreviousCounselSessionListRes;
import com.springboot.api.dto.counselsession.UpdateCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateCounselSessionRes;
import com.springboot.api.dto.counselsession.UpdateCounselorInCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateCounselorInCounselSessionRes;
import com.springboot.api.dto.counselsession.UpdateStatusInCounselSessionReq;
import com.springboot.api.dto.counselsession.UpdateStatusInCounselSessionRes;
import com.springboot.api.service.CounselSessionService;
import com.springboot.enums.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

                CommonRes<AddCounselSessionRes> commonRes = new CommonRes<>(addCounselSessionRes);
                return ResponseEntity.ok(commonRes);

        }

        @GetMapping("/list")
        @Operation(summary = "상담일정 목록 조회", tags = { "로그인/홈" })
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
        @Operation(summary = "상담일정 조회", tags = { "관리자 화면" })
        public ResponseEntity<CommonRes<SelectCounselSessionRes>> selectCounselSession(
                        @PathVariable String counselSessionId) {

                SelectCounselSessionRes selectCounselSessionRes = counselSessionService
                                .selectCounselSession(counselSessionId);

                return ResponseEntity.ok(new CommonRes<>(selectCounselSessionRes));

        }

        @PutMapping
        @Operation(summary = "상담일정 수정", tags = { "관리자 화면" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<UpdateCounselSessionRes>> updateCounselSession(
                        @RequestBody @Valid UpdateCounselSessionReq updateCounselSessionReq) {

                UpdateCounselSessionRes updateCounselSessionRes = counselSessionService
                                .updateCounselSession(updateCounselSessionReq);

                return ResponseEntity.ok(new CommonRes<>(updateCounselSessionRes));
        }

        @PutMapping("/counselor")
        @Operation(summary = "상담일정 담당 약사 수정", tags = {
                        "로그인/홈" }, description = "req body에 counselorId 넣지 않은 경우 로그인 계정 정보로 할당됨. ")
        @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
        public ResponseEntity<CommonRes<UpdateCounselorInCounselSessionRes>> updateCounselorInCounselSession(
                        @RequestBody @Valid UpdateCounselorInCounselSessionReq updateCounselorInCounselSessionReq) {

                UpdateCounselorInCounselSessionRes updateCounselorInCounselSessionRes = counselSessionService
                                .updateCounselorInCounselSession(
                                                updateCounselorInCounselSessionReq);

                return ResponseEntity.ok(new CommonRes<>(updateCounselorInCounselSessionRes));

        }

        @PutMapping("/status")
        @Operation(summary = "상담 상태 수정", tags = { "로그인/홈" })
        @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
        public ResponseEntity<CommonRes<UpdateStatusInCounselSessionRes>> updateStatusInCounselSession(
                        @RequestBody @Valid UpdateStatusInCounselSessionReq updateStatusInCounselSessionReq) {
                UpdateStatusInCounselSessionRes updateStatusInCounselSessionRes = counselSessionService
                                .updateStatusInCounselSession(updateStatusInCounselSessionReq);

                return ResponseEntity.ok(new CommonRes<>(updateStatusInCounselSessionRes));

        }

        @DeleteMapping
        @Operation(summary = "상담일정 삭제", tags = { "관리자 화면" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<DeleteCounselSessionRes>> deleteCounselSession(
                        @RequestBody @Valid DeleteCounselSessionReq deleteCounselSessionReq) {

                DeleteCounselSessionRes deleteCounselSessionRes = counselSessionService
                                .deleteCounselSessionRes(deleteCounselSessionReq);
                return ResponseEntity.ok(new CommonRes<>(deleteCounselSessionRes));

        }

        @GetMapping("/{counselSessionId}/previous/list")
        @Operation(summary = "이전 상담 내역 조회", tags = { "본상담 - 이전 상담 내역" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<List<SelectPreviousCounselSessionListRes>>> selectPreviousCounselSessionList(
                        @PathVariable("counselSessionId") String counselSessionId) {

                List<SelectPreviousCounselSessionListRes> selectPreviousListByCounselSessionIdResList = counselSessionService
                                .selectPreviousCounselSessionList(counselSessionId);

                return ResponseEntity.ok(new CommonRes<>(selectPreviousListByCounselSessionIdResList));

        }

}
