package com.springboot.api.dto.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
public class LoginRes {

    private long id;
    private List<String> roles;
}
