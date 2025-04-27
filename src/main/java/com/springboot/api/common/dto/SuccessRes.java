package com.springboot.api.common.dto;

import com.springboot.api.common.message.HttpMessages;

import lombok.Data;

@Data
public class SuccessRes {

    private String message = HttpMessages.SUCCESS;
}
