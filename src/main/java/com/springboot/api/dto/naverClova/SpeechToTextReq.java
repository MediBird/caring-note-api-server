package com.springboot.api.dto.naverClova;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class SpeechToTextReq {

    @Builder.Default
    private String language = "ko-KR";
    //completion optional, sync/async (응답 결과 반환 방식(sync/async) 설정, 필수 파라미터 아님)
    @Builder.Default
    private String completion = "sync";
    //optional, used to receive the analyzed results (분석된 결과 조회 용도, 필수 파라미터 아님)
    private String callback;
    //optional, any data (임의의 Callback URL 값 입력, 필수 파라미터 아님)
    private Map<String, Object> userdata;
    @Builder.Default
    private Boolean wordAlignment = true;
    @Builder.Default
    private Boolean fullText = true;
    //boosting object array (키워드 부스팅 객체 배열)
    private List<BoostingDTO> boostingDTOS;
    //comma separated words (쉼표 구분 키워드)
    private String forbiddens;
    private DiarizationDTO diarization;
    private SedDTO sed;

}
