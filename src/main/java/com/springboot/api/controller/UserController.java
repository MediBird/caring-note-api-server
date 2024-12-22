package com.springboot.api.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.api.common.util.JwtUtil;
import com.springboot.api.dto.user.AddUserReq;
import com.springboot.api.dto.user.AddUserRes;
import com.springboot.api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Deprecated
public class UserController {

    UserService userService;
    JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "회원가입",
             description = "권한,기본정보를 입력받아 회원가입 처리, 헤더에 토큰 응답",
             responses = {
                @ApiResponse(responseCode = "201", description = "회원가입 성공")
            }
    )
    @PostMapping("/v1/signup")
    public ResponseEntity<Void> addUser(
            @Parameter(description = "User details for the new user to be added", required = true)
            @RequestBody @Valid AddUserReq addUserReq) throws RuntimeException {

        AddUserRes addUserRes = userService.addUser(addUserReq);

        String token = jwtUtil.generateToken(addUserRes.getId(), addUserRes.getRoles());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }

    @Operation(summary = "로그인", description = "Keycloak 로그인 페이지로 리다이렉트",
            responses = {
                @ApiResponse(responseCode = "302", description = "Keycloak 로그인 페이지로 리다이렉트")
            }, tags = {"로그인/홈"})
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String keycloakLoginUrl = "http://caringnote.co.kr/keycloak/realms/caringnote/protocol/openid-connect/auth"
                + "?client_id=localhost"
                + "&response_type=code";

        response.sendRedirect(keycloakLoginUrl);
    }

}
