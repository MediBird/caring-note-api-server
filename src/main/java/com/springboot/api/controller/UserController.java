package com.springboot.api.controller;

import com.springboot.api.common.util.JwtUtil;
import com.springboot.api.dto.user.AddUserReq;
import com.springboot.api.dto.user.AddUserRes;
import com.springboot.api.dto.user.LoginReq;
import com.springboot.api.dto.user.LoginRes;
import com.springboot.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Add a new user", description = "Adds a new user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<?> addUser(
            @Parameter(description = "User details for the new user to be added", required = true)
            @RequestBody @Valid AddUserReq addUserReq) throws RuntimeException{


        AddUserRes addUserRes = userService.addUser(addUserReq);

        String token = jwtUtil.generateToken(addUserRes.getId(),addUserRes.getRoles());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return new ResponseEntity<>(null, headers, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReq loginReq){

        LoginRes loginRes = userService.login(loginReq);

        String token = jwtUtil.generateToken(loginRes.getId(), loginRes.getRoles());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return new ResponseEntity<>(null, headers, HttpStatus.CREATED);

    }


}
