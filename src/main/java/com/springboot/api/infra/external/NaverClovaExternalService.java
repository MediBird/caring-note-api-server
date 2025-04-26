package com.springboot.api.infra.external;

import com.springboot.api.counselsession.dto.naverClova.SpeechToTextReq;
import com.springboot.api.counselsession.dto.naverClova.SpeechToTextRes;
import java.util.Map;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.PostExchange;

public interface NaverClovaExternalService {

    @PostExchange("/recognizer/upload")
    ResponseEntity<SpeechToTextRes> convertSpeechToText(@RequestHeader Map<String, String> headers,
        @RequestPart("media") FileSystemResource mediaFile,
        @RequestPart("params") SpeechToTextReq speechToTextReq);
}
