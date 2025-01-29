package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counseleeconsent.*;
import com.springboot.api.service.CounseleeConsentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ApiController(path = "/v1/counselee/consent", name = "CounseleeConsentController", description = "내담자 동의 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class CounseleeConsentController {

    private final CounseleeConsentService counseleeConsentService;

    @GetMapping("/{counseleeId}")
    @Operation(summary = "내담자 개인정보 수집 동의 여부 조회", tags = { "개인 정보 수집 동의" })
    public ResponseEntity<CommonRes<SelectCounseleeConsentByCounseleeIdRes>> selectCounseleeConsentByCounseleeId(
            @PathVariable(required = true) String counseleeId) {

        SelectCounseleeConsentByCounseleeIdRes selectCounseleeConsentByCounseleeIdRes = counseleeConsentService
                .selectCounseleeConsentByCounseleeId(counseleeId);

        return ResponseEntity.ok(new CommonRes<>(selectCounseleeConsentByCounseleeIdRes));
    }

    @PostMapping
    @Operation(summary = "내담자 개인정보 수집 동의 여부 등록", tags = { "개인 정보 수집 동의" })
    public ResponseEntity<CommonRes<AddCounseleeConsentRes>> addCounseleeConsent(
            @RequestBody @Valid AddCounseleeConsentReq addCounseleeConsentReq) {

        AddCounseleeConsentRes addCounseleeConsentRes = counseleeConsentService.addCounseleeConsent(
                addCounseleeConsentReq);

        return ResponseEntity.ok(new CommonRes<>(addCounseleeConsentRes));
    }

    @PutMapping
    @Operation(summary = "내담자 개인정보 수집 동의 여부 수정", tags = { "개인 정보 수집 동의" })
    public ResponseEntity<CommonRes<UpdateCounseleeConsentRes>> updateCounseleeConsent(
            @RequestBody @Valid UpdateCounseleeConsentReq updateCounseleeConsentReq) {

        UpdateCounseleeConsentRes updateCounseleeConsentRes = counseleeConsentService.updateCounseleeConsent(
                updateCounseleeConsentReq);

        return ResponseEntity.ok(new CommonRes<>(updateCounseleeConsentRes));

    }

    @DeleteMapping
    @Operation(summary = "내담자 개인정보 수집 동의 여부 삭제")
    public ResponseEntity<CommonRes<DeleteCounseleeConsentRes>> deleteCounseleeConsent(
            @RequestBody @Valid DeleteCounseleeConsentReq deleteCounseleeConsentReq) {

        DeleteCounseleeConsentRes deleteCounseleeConsentRes = counseleeConsentService
                .deleteCounseleeConsent(deleteCounseleeConsentReq);

        return ResponseEntity.ok(new CommonRes<>(deleteCounseleeConsentRes));

    }

}
