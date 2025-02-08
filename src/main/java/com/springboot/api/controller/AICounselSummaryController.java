package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.SuccessRes;
import com.springboot.api.dto.medicationcounsel.ConvertSpeechToTextReq;
import com.springboot.api.service.AICounselSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@ApiController(
        name = "AICounselSummaryController",
        path = "/v1/counsel/ai",
        description = "본상담 내 AI요약 관련 API를 제공하는 Controller"
)
@RequiredArgsConstructor
public class AICounselSummaryController {

    private final AICounselSummaryService aiCounselSummaryService;

    @PostMapping("/stt")
    @Operation(summary = "convert Speech to Text", tags ={"본상담 - 복약 상담"})
    public ResponseEntity<SuccessRes> convertSpeechToText(
            @RequestPart("audio") MultipartFile file
            , @RequestPart("body") @Valid ConvertSpeechToTextReq convertSpeechToTextReq
    ){
        aiCounselSummaryService.convertSpeechToText(file, convertSpeechToTextReq);

        return ResponseEntity.ok(new SuccessRes());
    }
}
