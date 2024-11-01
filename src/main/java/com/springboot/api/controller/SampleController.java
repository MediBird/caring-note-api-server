package com.springboot.api.controller;


import com.springboot.api.common.annotation.RoleSecured;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleController {

    @GetMapping("/v1/auth")
    @ResponseStatus(HttpStatus.OK)
    @RoleSecured("ADMIN")
    public String auth() {

        return "권한 있습니다.";

    }


}
