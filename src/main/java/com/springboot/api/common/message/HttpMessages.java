package com.springboot.api.common.message;

public class HttpMessages {

    public static final String SUCCESS = "응답 성공하였습니다.";
    public static final String BAD_REQUEST = "잘못된 요청입니다.";
    public static final String BAD_REQUEST_PARAMETER = "요청 파라미터 값이 올바르지 않습니다.";
    public static final String INVALID_REQUEST_BODY = "요청 바디 값 검증을 통과하지 못하였습니다.";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 오류 입니다. 관리자에 문의하시기 바랍니다.";
    public static final String NOT_FOUND = "존재하지 않는 페이지입니다.";
    public static final String UNAUTHORIZED = "접근 권한이 없습니다.";
    public static final String CONFLICT_DUPLICATE ="저장하려는 데이터가 이미 존재합니다.";
}
