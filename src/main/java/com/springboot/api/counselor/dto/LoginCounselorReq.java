package com.springboot.api.counselor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginCounselorReq {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
