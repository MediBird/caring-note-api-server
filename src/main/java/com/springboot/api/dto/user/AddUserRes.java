package com.springboot.api.dto.user;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddUserRes {

    private long id;
    private List<String> roles;

}
