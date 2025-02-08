package com.springboot.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.common.dto.SuccessRes;
import com.springboot.api.dto.aiCounselSummary.SelectSpeakerListRes;
import com.springboot.api.dto.aiCounselSummary.SelectSpeechToTextRes;
import com.springboot.api.dto.medicationcounsel.ConvertSpeechToTextReq;
import com.springboot.api.service.AICounselSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiController(
        name = "AICounselSummaryController",
        path = "/v1/counsel/ai",
        description = "본상담 내 AI요약 관련 API를 제공하는 Controller"
)
@RequiredArgsConstructor
public class AICounselSummaryController {

    private final AICounselSummaryService aiCounselSummaryService;

    @PostMapping("/stt")
    @Operation(summary = "convert Speech to Text", tags ={"AI요약"})
    public ResponseEntity<SuccessRes> convertSpeechToText(
            @RequestPart("audio") MultipartFile file
            , @RequestPart("body") @Valid ConvertSpeechToTextReq convertSpeechToTextReq
    ){
        aiCounselSummaryService.convertSpeechToText(file, convertSpeechToTextReq);

        return ResponseEntity.ok(new SuccessRes());
    }

    @GetMapping("{counselSessionId}/stt/speaker/list")
    @Operation(summary = "발화자 별 발화 내용 조회",tags={"AI요약"})
    public ResponseEntity<CommonRes<List<SelectSpeakerListRes>>> selectSpeakerList(
            @PathVariable String counselSessionId
    ) throws JsonProcessingException {
        List<SelectSpeakerListRes> selectSpeakerListResList = aiCounselSummaryService.selectSpeakerList(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectSpeakerListResList));
    }

    @GetMapping("{counselSessionId}/stt")
    @Operation(summary = "stt 결과 조회",tags={"AI요약"})
    public ResponseEntity<CommonRes<List<SelectSpeechToTextRes>>> selectSpeechToText(
            @PathVariable String counselSessionId
    ) throws JsonProcessingException {

        List<SelectSpeechToTextRes> selectSpeechToTextResList = aiCounselSummaryService.selectSpeechToText(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectSpeechToTextResList));
    }




}
