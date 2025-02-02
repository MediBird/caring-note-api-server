package com.springboot.api.service;


//secret-key : 796d7a638753441eb241a266f1f10d49
//invoke URL : https://clovaspeech-gw.ncloud.com/external/v1/10309/338f74c076c81a57b47313f867ab35519c289e3f8dede43066bea9f266708cb1

import com.springboot.api.dto.naverClova.SpeechToTextReq;
import com.springboot.api.dto.naverClova.SpeechToTextRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

public interface NaverClovaService {

    @PostExchange("/recognizer/upload")
    ResponseEntity<SpeechToTextRes> transformSpeechToText(@RequestHeader Map<String,String> headers,
                                                          @RequestPart("media") MultipartFile multipartFile,
                                                          @RequestPart("params") SpeechToTextReq speechToTextReq);
}
