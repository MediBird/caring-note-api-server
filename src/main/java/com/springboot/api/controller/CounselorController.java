package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.util.JwtUtil;
import com.springboot.api.dto.counselor.AddCounselorReq;
import com.springboot.api.dto.counselor.AddCounselorRes;
import com.springboot.api.dto.counselor.LoginCounselorReq;
import com.springboot.api.dto.counselor.LoginCounselorRes;
import com.springboot.api.service.CounselorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ApiController(
        name = "CounselorController"
        ,description = "유저 관리 API를 제공하는 Controller"
        ,path = "/v1/counselor"
)
@RequiredArgsConstructor
public class CounselorController {

    private final CounselorService counselorService;
    private final JwtUtil jwtUtil;


    @Operation(summary = "사용자 추가",
            description = "권한,기본정보를 입력받아 회원가입 처리, 헤더에 토큰 응답",
            responses = {
                @ApiResponse(responseCode = "201", description = "회원가입 성공")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<Void> addCounselor(
            @Parameter(description = "Details of the new Counselor to be added", required = true)
            @RequestBody @Valid AddCounselorReq addCounselorReq) throws RuntimeException {

        AddCounselorRes addCounselorRes = counselorService.addCounselor(addCounselorReq);

        return jwtUtil.createTokenResponse(addCounselorRes.id(), addCounselorRes.roleType());

    }

    @Operation(summary = "로그인", description = "로그인 처리, 헤더에 토큰 응답",
            responses = {
                @ApiResponse(responseCode = "201", description = "로그인 성공")
            }, tags = {"로그인/홈"})
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> login(@RequestBody @Valid LoginCounselorReq loginCounselorReq) throws RuntimeException {

        LoginCounselorRes loginCounselorRes = counselorService.loginCounselor(loginCounselorReq);

        return jwtUtil.createTokenResponse(loginCounselorRes.counselorId(), loginCounselorRes.roleType());
    }

}
