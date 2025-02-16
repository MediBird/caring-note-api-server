package com.springboot.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.common.dto.SuccessRes;
import com.springboot.api.dto.aiCounselSummary.*;
import com.springboot.api.service.AICounselSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@ApiController(name = "AICounselSummaryController", path = "/v1/counsel/ai", description = "본상담 내 AI요약 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class AICounselSummaryController {

    private final AICounselSummaryService aiCounselSummaryService;

    @PostMapping(value = "/stt", consumes = "multipart/form-data")
    @Operation(summary = "convert Speech to Text", tags = { "AI요약" })
    public ResponseEntity<SuccessRes> convertSpeechToText(
            @RequestPart("audio") MultipartFile file,
            @RequestPart("body") @Valid ConvertSpeechToTextReq convertSpeechToTextReq) {
        aiCounselSummaryService.convertSpeechToText(file, convertSpeechToTextReq);
        return ResponseEntity.ok(new SuccessRes());

    }

    @GetMapping("{counselSessionId}/stt/speaker/list")
    @Operation(summary = "발화자 별 발화 내용 조회", tags = { "AI요약" })
    public ResponseEntity<CommonRes<List<SelectSpeakerListRes>>> selectSpeakerList(
            @PathVariable String counselSessionId) throws JsonProcessingException {
        List<SelectSpeakerListRes> selectSpeakerListResList = aiCounselSummaryService
                .selectSpeakerList(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectSpeakerListResList));
    }

    @GetMapping("{counselSessionId}/stt")
    @Operation(summary = "stt 결과 조회", tags = { "AI요약" })
    public ResponseEntity<CommonRes<List<SelectSpeechToTextRes>>> selectSpeechToText(
            @PathVariable String counselSessionId) throws JsonProcessingException {

        List<SelectSpeechToTextRes> selectSpeechToTextResList = aiCounselSummaryService
                .selectSpeechToText(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectSpeechToTextResList));
    }


    @GetMapping("{counselSessionId}/ta")
    @Operation(summary = "ta 결과 조회",tags={"AI요약"})
    public ResponseEntity<CommonRes<SelectAnalysedTextRes>> selectAnalysedText(
            @PathVariable String counselSessionId
    ) throws JsonProcessingException {

        SelectAnalysedTextRes selectAnalysedTextRes = aiCounselSummaryService.selectAnalysedText(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectAnalysedTextRes));
    }


    @PostMapping("/ta")
    @Operation(summary = "선택 발화자 기준 TA",tags = {"AI요약"})
    public ResponseEntity<SuccessRes> analyseText(
            @Valid @RequestBody AnalyseTextReq analyseTextReq) throws JsonProcessingException {

        aiCounselSummaryService.analyseText(analyseTextReq);
        return ResponseEntity.ok(new SuccessRes());

    }


    @DeleteMapping
    @Operation(summary = "counselSessionId 기준 AI요약 삭제",tags = {"AI요약"})
    public ResponseEntity<SuccessRes> deleteAICounselSummary(
            @RequestBody DeleteAICounselSummaryReq deleteAICounselSummaryReq
    ){

        aiCounselSummaryService.deleteAICounselSummary(deleteAICounselSummaryReq);
        return ResponseEntity.ok(new SuccessRes());
    }


    @GetMapping("{counselSessionId}/popup")
    @Operation(summary = "녹음 popup 조건 충족 여부",tags = {"AI요약"})
    public ResponseEntity<CommonRes<SelectAICounselSummaryPopUpRes>> selectAICounselSummaryPopUp(
            @PathVariable String counselSessionId,
            @RequestParam(required = false) LocalDate baseDate
    ){

        SelectAICounselSummaryPopUpRes selectAICounselSummaryPopUpRes = aiCounselSummaryService.selectAICounselSummaryPopUp(counselSessionId, baseDate);
        return ResponseEntity.ok(new CommonRes<>(selectAICounselSummaryPopUpRes));
    }

    @GetMapping("{counselSessionId}/status")
    @Operation(summary = "AI 요약 상태 조회",tags={"AI요약"})
    public ResponseEntity<CommonRes<SelectAICounselSummaryStatusRes>> selectAICounselSummaryStatus(
            @PathVariable String counselSessionId
    ){
        SelectAICounselSummaryStatusRes selectAICounselSummaryStatusRes = aiCounselSummaryService.selectAICounselSummaryStatus(counselSessionId);
        return ResponseEntity.ok(new CommonRes<>(selectAICounselSummaryStatusRes));
    }


}
