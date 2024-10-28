package com.springboot.api.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginReq {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
