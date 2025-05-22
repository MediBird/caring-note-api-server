package com.springboot.api.counselsession.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.common.dto.SuccessRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.ConvertSpeechToTextReq;
import com.springboot.api.counselsession.dto.aiCounselSummary.DeleteAICounselSummaryReq;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectAICounselSummaryPopUpRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectAICounselSummaryStatusRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectAnalysedTextRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectSpeechToTextRes;
import com.springboot.api.counselsession.service.AICounselSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@ApiController(name = "AICounselSummaryController", path = "/v1/counsel/ai", description = "본상담 내 AI요약 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class AICounselSummaryController {

    private final AICounselSummaryService aiCounselSummaryService;

    @Deprecated
    @PostMapping(value = "/stt", consumes = "multipart/form-data")
    @Operation(summary = "convert Speech to Text", tags = {"AI요약"})
    public ResponseEntity<SuccessRes> convertSpeechToText(
        @RequestPart("audio") MultipartFile file,
        @RequestPart("body") @Valid ConvertSpeechToTextReq convertSpeechToTextReq) throws IOException {
        aiCounselSummaryService.convertSpeechToText(file, convertSpeechToTextReq);
        return ResponseEntity.ok(new SuccessRes());

    }

    @PostMapping(value = "{counselSessionId}/stt")
    @Operation(summary = "음성 파일 STT 변환", tags = {"AI요약"})
    public ResponseEntity<SuccessRes> convertSpeechToText(
        @PathVariable String counselSessionId) {
        aiCounselSummaryService.convertSpeechToText(counselSessionId);
        return ResponseEntity.ok(new SuccessRes());
    }

    @GetMapping("{counselSessionId}/stt")
    @Operation(summary = "stt 결과 조회", tags = {"AI요약"})
    public ResponseEntity<CommonRes<List<SelectSpeechToTextRes>>> selectSpeechToText(
        @PathVariable String counselSessionId) throws JsonProcessingException {

        List<SelectSpeechToTextRes> selectSpeechToTextResList = aiCounselSummaryService
            .selectSpeechToText(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectSpeechToTextResList));
    }

    @GetMapping("{counselSessionId}/ta")
    @Operation(summary = "ta 결과 조회", tags = {"AI요약"})
    public ResponseEntity<CommonRes<SelectAnalysedTextRes>> selectAnalysedText(
        @PathVariable String counselSessionId) {

        SelectAnalysedTextRes selectAnalysedTextRes = aiCounselSummaryService.selectAnalysedText(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectAnalysedTextRes));
    }

    @DeleteMapping
    @Operation(summary = "counselSessionId 기준 AI요약 삭제", tags = {"AI요약"})
    public ResponseEntity<SuccessRes> deleteAICounselSummary(
        @RequestBody DeleteAICounselSummaryReq deleteAICounselSummaryReq) {

        aiCounselSummaryService.deleteAICounselSummary(deleteAICounselSummaryReq);
        return ResponseEntity.ok(new SuccessRes());
    }

    @GetMapping("{counselSessionId}/popup")
    @Operation(summary = "녹음 popup 조건 충족 여부", tags = {"AI요약"})
    public ResponseEntity<CommonRes<SelectAICounselSummaryPopUpRes>> selectAICounselSummaryPopUp(
        @PathVariable String counselSessionId,
        @RequestParam(required = false) LocalDate baseDate) {

        SelectAICounselSummaryPopUpRes selectAICounselSummaryPopUpRes = aiCounselSummaryService
            .selectAICounselSummaryPopUp(counselSessionId, baseDate);
        return ResponseEntity.ok(new CommonRes<>(selectAICounselSummaryPopUpRes));
    }

    @GetMapping("{counselSessionId}/status")
    @Operation(summary = "AI 요약 상태 조회", tags = {"AI요약"})
    public ResponseEntity<CommonRes<SelectAICounselSummaryStatusRes>> selectAICounselSummaryStatus(
        @PathVariable String counselSessionId) {
        SelectAICounselSummaryStatusRes selectAICounselSummaryStatusRes = aiCounselSummaryService
            .selectAICounselSummaryStatus(counselSessionId);
        return ResponseEntity.ok(new CommonRes<>(selectAICounselSummaryStatusRes));
    }

}
