package com.springboot.api.controller;

import com.springboot.api.common.util.JwtUtil;
import com.springboot.api.dto.user.AddUserReq;
import com.springboot.api.dto.user.AddUserRes;
import com.springboot.api.dto.user.LoginReq;
import com.springboot.api.dto.user.LoginRes;
import com.springboot.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {
    UserService userService;
    JwtUtil jwtUtil;


    public UserController(UserService userService, JwtUtil jwtUtil){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "회원가입"
            , description = "권한,기본정보를 입력받아 회원가입 처리, 헤더에 토큰 응답"
            , responses = {
                @ApiResponse(responseCode = "201", description = "회원가입 성공")
             }
    )
    @PostMapping("/v1/signup")
    public ResponseEntity<Void> addUser(
            @Parameter(description = "User details for the new user to be added", required = true)
            @RequestBody @Valid AddUserReq addUserReq) throws RuntimeException{


        AddUserRes addUserRes = userService.addUser(addUserReq);

        String token = jwtUtil.generateToken(addUserRes.getId(),addUserRes.getRoles());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }
    @Operation(summary = "로그인", description = "로그인 처리, 헤더에 토큰 응답"
            , responses = {
            @ApiResponse(responseCode = "201", description = "로그인 성공")
    })
    @PostMapping("/v1/login")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> login(@RequestBody @Valid LoginReq loginReq){

        LoginRes loginRes = userService.login(loginReq);

        String token = jwtUtil.generateToken(loginRes.getId(), loginRes.getRoles());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }

}
