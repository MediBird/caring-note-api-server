package com.springboot.api.controller;


import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonCursorRes;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.common.util.AuthUtil;
import com.springboot.api.dto.counselsession.*;
import com.springboot.api.service.CounselSessionService;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@ApiController(
        name = "CounselSessionController"
        ,description = "상담 세션 관련 API를 제공하는 Controller"
        ,path = "/v1/counsel/session"
)
@RequiredArgsConstructor
public class CounselSessionController {

    private final CounselSessionService counselSessionService;
    private final AuthUtil authUtil;


    @Operation(summary = "상담세션(일정) 추가",tags = {"관리자 화면"})
    @PostMapping
    @RoleSecured(RoleType.ROLE_ADMIN)
    public ResponseEntity<CommonRes<AddCounselSessionRes>> addCounselSession(@AuthenticationPrincipal UserDetails userDetails
            , @RequestBody @Valid AddCounselSessionReq addCounselSessionReq) {

        AddCounselSessionRes addCounselSessionRes = counselSessionService
                .addCounselSession(userDetails.getUsername(), addCounselSessionReq);

        CommonRes<AddCounselSessionRes> commonRes = new CommonRes<>(addCounselSessionRes);
        return ResponseEntity.ok(commonRes);

    }

    @GetMapping("/list")
    @Operation(summary = "상담일정 목록 조회", tags = {"로그인/홈"})
    public ResponseEntity<CommonCursorRes<List<SelectCounselSessionListItem>>> selectCounselSessionListByBaseDateAndCursorAndSize(@AuthenticationPrincipal UserDetails userDetails
            , @RequestParam(required = false) LocalDate baseDate
            , @RequestParam(required = false) String cursor
            , @RequestParam(defaultValue = "15") int size)  {

        SelectCounselSessionListByBaseDateAndCursorAndSizeReq selectCounselSessionListByBaseDateAndCursorAndSizeReq
                = SelectCounselSessionListByBaseDateAndCursorAndSizeReq
                .builder()
                .baseDate(baseDate)
                .cursor(cursor)
                .size(size)
                .build();

        RoleType roleType = authUtil.getRoleType(userDetails);

        SelectCounselSessionListByBaseDateAndCursorAndSizeRes selectCounselSessionListByBaseDateAndCursorAndSizeRes = counselSessionService
                .selectCounselSessionListByBaseDateAndCursorAndSize(userDetails.getUsername(),roleType, selectCounselSessionListByBaseDateAndCursorAndSizeReq);

        return ResponseEntity.ok(new CommonCursorRes<>(
                selectCounselSessionListByBaseDateAndCursorAndSizeRes.sessionListItems(), selectCounselSessionListByBaseDateAndCursorAndSizeRes.nextCursor(), selectCounselSessionListByBaseDateAndCursorAndSizeRes.hasNext()
        ));
    }


    @GetMapping("/{counselSessionId}")
    @Operation(summary = "상담일정 조회", tags = {"관리자 화면"})
    public ResponseEntity<CommonRes<SelectCounselSessionRes>> selectCounselSession(@AuthenticationPrincipal UserDetails userDetails,
                                                                        @PathVariable  String counselSessionId) {

        SelectCounselSessionRes selectCounselSessionRes = counselSessionService.selectCounselSession(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectCounselSessionRes));

    }


    @PutMapping
    @Operation(summary = "상담일정 수정", tags = {"관리자 화면"})
    @RoleSecured(RoleType.ROLE_ADMIN)
    public ResponseEntity<CommonRes<UpdateCounselSessionRes>> updateCounselSession(@AuthenticationPrincipal UserDetails userDetails
            ,@RequestBody @Valid UpdateCounselSessionReq updateCounselSessionReq) {

        UpdateCounselSessionRes updateCounselSessionRes = counselSessionService.updateCounselSession(updateCounselSessionReq);

        return ResponseEntity.ok(new CommonRes<>(updateCounselSessionRes));
    }


    @DeleteMapping
    @Operation(summary = "상담일정 삭제", tags = {"관리자 화면"})
    @RoleSecured(RoleType.ROLE_ADMIN)
    public ResponseEntity<CommonRes<DeleteCounselSessionRes>> deleteCounselSession(@AuthenticationPrincipal UserDetails userDetails
            ,@RequestBody @Valid DeleteCounselSessionReq deleteCounselSessionReq) {

        DeleteCounselSessionRes deleteCounselSessionRes = counselSessionService.deleteCounselSessionRes(deleteCounselSessionReq);
        return ResponseEntity.ok(new CommonRes<>(deleteCounselSessionRes));

    }

    @GetMapping("/{counselSessionId}/previous/list")
    @Operation(summary = "이전 상담 내역 조회", tags = {"본상담 - 이전 상담 내역"})
    @RoleSecured(RoleType.ROLE_ADMIN)
    public ResponseEntity<CommonRes<List<SelectPreviousCounselSessionListRes>>> selectPreviousCounselSessionList(@AuthenticationPrincipal UserDetails userDetails
            , @PathVariable("counselSessionId")String counselSessionId) {

        List<SelectPreviousCounselSessionListRes> selectPreviousListByCounselSessionIdResList
                = counselSessionService.selectPreviousCounselSessionList(userDetails.getUsername(),counselSessionId);


        return ResponseEntity.ok(new CommonRes<>(selectPreviousListByCounselSessionIdResList));

    }

}
