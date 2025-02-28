package com.springboot.api.counselor.dto;

import java.util.List;

public record CounselorNameListRes(
        List<String> counselorNames) {
}