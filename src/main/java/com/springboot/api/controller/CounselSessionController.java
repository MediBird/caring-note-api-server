package com.springboot.api.controller;


import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.dto.counselsession.AddCounselSessionReq;
import com.springboot.api.dto.counselsession.AddCounselSessionRes;
import com.springboot.api.service.CounselSessionService;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/counsel/session")
@Tag(name = "CounselSessionController", description = "상담 세션 관련 API를 제공하는 Controller")
public class CounselSessionController {

    private final CounselSessionService counselSessionService;

    public CounselSessionController(CounselSessionService counselSessionService) {
        this.counselSessionService = counselSessionService;
    }

    //TODO 상담일정 등록
    @Operation(summary = "상담세션(일정) 추가",tags = {"관리자 화면"})
    @PostMapping
    @RoleSecured(RoleType.ADMIN)
    public ResponseEntity<AddCounselSessionRes> addCounselSession(@AuthenticationPrincipal UserDetails userDetails
            , @RequestBody @Valid AddCounselSessionReq addCounselSessionReq) throws RuntimeException {

        AddCounselSessionRes addCounselSessionRes = counselSessionService
                .addCounselSession(userDetails.getUsername(), addCounselSessionReq);

        return ResponseEntity.ok(addCounselSessionRes);

    }

//    @GetMapping
//    public ResponseEntity<SelectCounselSessionRes> selectCounselSession() {
//        // 예약 처리 로직 구현
//        return
//
//    }
//
//    @GetMapping("/list")
//    public ResponseEntity<SelectCounselSessionListRes> selectCounselSessionList() {
//        // 예약 처리 로직 구현
//        return
//
//    }
//
//
//    //TODO 상담일정 수정
//    @PostMapping
//    public ResponseEntity<UpdateCounselSessionRes> updateCounselSession(UpdateCounselSessionReq updateCounselSessionReq) {
//        // 예약 처리 로직 구현
//        return
//
//    }
//
//    //TODO 상당일정 조회
//    public ResponseEntity<DeleteCounselSessionRes> deleteCounselSession(DeleteCounselSessionReq deleteCounselSessionReq) {
//        // 예약 처리 로직 구현
//        return
//
//    }






}
