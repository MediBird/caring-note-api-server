package com.springboot.api.infra.external;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.PostExchange;

import com.springboot.api.counselsession.dto.naverClova.SpeechToTextReq;
import com.springboot.api.counselsession.dto.naverClova.SpeechToTextRes;

public interface NaverClovaExternalService {

    @PostExchange("/recognizer/upload")
    ResponseEntity<SpeechToTextRes> convertSpeechToText(@RequestHeader Map<String, String> headers,
            @RequestPart("media") MultipartFile multipartFile,
            @RequestPart("params") SpeechToTextReq speechToTextReq);
}
