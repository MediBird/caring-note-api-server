package com.springboot.api.controller;


import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonCursorRes;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.common.util.AuthUtil;
import com.springboot.api.dto.counselsession.*;
import com.springboot.api.service.CounselSessionService;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/counsel/session")
@Tag(name = "CounselSessionController", description = "상담 세션 관련 API를 제공하는 Controller")
public class CounselSessionController {

    private final CounselSessionService counselSessionService;
    private final AuthUtil authUtil;

    public CounselSessionController(CounselSessionService counselSessionService, AuthUtil authUtil) {
        this.counselSessionService = counselSessionService;
        this.authUtil = authUtil;
    }

    @Operation(summary = "상담세션(일정) 추가",tags = {"관리자 화면"})
    @PostMapping
    @RoleSecured(RoleType.ADMIN)
    public ResponseEntity<CommonRes<AddCounselSessionRes>> addCounselSession(@AuthenticationPrincipal UserDetails userDetails
            , @RequestBody @Valid AddCounselSessionReq addCounselSessionReq) throws RuntimeException {

        AddCounselSessionRes addCounselSessionRes = counselSessionService
                .addCounselSession(userDetails.getUsername(), addCounselSessionReq);

        CommonRes<AddCounselSessionRes> commonRes = new CommonRes<>(addCounselSessionRes);
        return ResponseEntity.ok(commonRes);

    }

//    @GetMapping
//    public ResponseEntity<SelectCounselSessionRes> selectCounselSession() {
//        return
//
//    }
//
    @GetMapping("/list")
    @Operation(summary = "상담일정 목록 조회", tags = {"로그인/홈"})
    public ResponseEntity<CommonCursorRes<List<SelectCounselSessionListItem>>> selectCounselSessionList(@AuthenticationPrincipal UserDetails userDetails
            , @RequestParam(required = false) LocalDateTime baseDateTime
            , @RequestParam(required = false) String cursorId
            , @RequestParam(defaultValue = "15") int size) throws RuntimeException {

        SelectCounselSessionListReq selectCounselSessionListReq
                = SelectCounselSessionListReq
                .builder()
                .baseDateTime(baseDateTime)
                .cursorId(cursorId)
                .size(size)
                .build();

        RoleType roleType = authUtil.getRoleType(userDetails);

        SelectCounselSessionListRes selectCounselSessionListRes = counselSessionService
                .selectCounselSessionList(userDetails.getUsername(),roleType, selectCounselSessionListReq);

        return ResponseEntity.ok(new CommonCursorRes<>(
                selectCounselSessionListRes.sessionListItems(), selectCounselSessionListRes.nextCursor(), selectCounselSessionListRes.hasNext()
        ));
    }
//
//
//    //TODO 상담일정 수정
//    @PostMapping
//    public ResponseEntity<UpdateCounselSessionRes> updateCounselSession(UpdateCounselSessionReq updateCounselSessionReq) {
//        return
//
//    }
//
//    //TODO 상당일정 조회
//    public ResponseEntity<DeleteCounselSessionRes> deleteCounselSession(DeleteCounselSessionReq deleteCounselSessionReq) {
//        return
//
//    }






}
