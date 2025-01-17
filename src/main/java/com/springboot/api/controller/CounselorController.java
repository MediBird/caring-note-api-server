package com.springboot.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.dto.counselor.GetCounselorRes;
import com.springboot.api.service.CounselorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@ApiController(name = "CounselorController", description = "유저 관리 API를 제공하는 Controller", path = "/v1/counselor")
@RequiredArgsConstructor
public class CounselorController {

    private final CounselorService counselorService;

    // @Operation(summary = "사용자 추가",
    //         description = "권한,기본정보를 입력받아 회원가입 처리, 헤더에 토큰 응답",
    //         responses = {
    //             @ApiResponse(responseCode = "201", description = "회원가입 성공")
    //         }
    // )
    // @PostMapping("/signup")
    // public ResponseEntity<Void> addCounselor(
    //         @Parameter(description = "Details of the new Counselor to be added", required = true)
    //         @RequestBody @Valid AddCounselorReq addCounselorReq) throws RuntimeException {
    //     AddCounselorRes addCounselorRes = counselorService.addCounselor(addCounselorReq);
    //     return jwtUtil.createTokenResponse(addCounselorRes.id(), addCounselorRes.roleType());
    // }
    // @Operation(summary = "로그인", description = "Keycloak 로그인 페이지로 리다이렉트",
    //         responses = {
    //             @ApiResponse(responseCode = "302", description = "Keycloak 로그인 페이지로 리다이렉트")
    //         }, tags = {"로그인/홈"})
    // @GetMapping("/login")
    // public void login(HttpServletResponse response) throws IOException {
    //     response.sendRedirect("/oauth2/authorization/keycloak");
    // }
    @Operation(summary = "내 정보 조회",
            description = "내 정보를 조회한다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "내 정보 조회 성공")
            })
    @GetMapping("/my-info")
    public ResponseEntity<GetCounselorRes> getMyInfo() {
        return ResponseEntity.ok(counselorService.getMyInfo());
    }
}
