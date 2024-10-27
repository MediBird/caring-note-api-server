package com.springboot.api.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddUserRes {

    private long id;
    private List<String> roles;

}
