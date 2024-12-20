package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counseleeconsent.*;
import com.springboot.api.service.CounseleeConsentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@ApiController(
        path="/v1/counselee/consent"
        ,name = "CounseleeConsentController"
        ,description = "내담자 동의 관련 API를 제공하는 Controller"
)
@RequiredArgsConstructor
public class CounseleeConsentController {

    private final CounseleeConsentService counseleeConsentService;


    @GetMapping("/{counselSessionId}")
    @Operation(summary = "내담자 개인정보 수집 동의 여부 조회",tags = {"개인 정보 수집 동의"})
    public ResponseEntity<CommonRes<SelectCounseleeConsentRes>> selectCounseleeConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String counselSessionId,
            @RequestParam(required = true) String counseleeId
    ) throws RuntimeException{

        SelectCounseleeConsentRes selectCounseleeConsentRes =  counseleeConsentService.selectCounseleeConsent(
                userDetails.getUsername()
                , counselSessionId
                ,counseleeId);

        return ResponseEntity.ok(new CommonRes<>(selectCounseleeConsentRes));
    }

    @PostMapping
    @Operation(summary = "내담자 개인정보 수집 동의 여부 등록",tags = {"개인 정보 수집 동의"})
    public ResponseEntity<CommonRes<AddCounseleeConsentRes>> addCounseleeConsent(
            @AuthenticationPrincipal UserDetails userDetails
            ,@RequestBody @Valid AddCounseleeConsentReq addCounseleeConsentReq) throws RuntimeException {

        AddCounseleeConsentRes addCounseleeConsentRes =  counseleeConsentService.addCounseleeConsent(
                userDetails.getUsername()
                , addCounseleeConsentReq);

        return ResponseEntity.ok(new CommonRes<>(addCounseleeConsentRes));
    }

    @PutMapping
    @Operation(summary = "내담자 개인정보 수집 동의 여부 수정",tags = {"개인 정보 수집 동의"})
    public ResponseEntity<CommonRes<UpdateCounseleeConsentRes>> updateCounseleeConsent(
            @AuthenticationPrincipal UserDetails userDetails
            ,@RequestBody @Valid UpdateCounseleeConsentReq updateCounseleeConsentReq)  throws RuntimeException{

        UpdateCounseleeConsentRes updateCounseleeConsentRes =  counseleeConsentService.updateCounseleeConsent(
                userDetails.getUsername()
                , updateCounseleeConsentReq);

        return ResponseEntity.ok(new CommonRes<>(updateCounseleeConsentRes));

    }

    @DeleteMapping
    @Operation(summary = "내담자 개인정보 수집 동의 여부 삭제")
    public ResponseEntity<CommonRes<DeleteCounseleeConsentRes>> deleteCounseleeConsent(
            @AuthenticationPrincipal UserDetails userDetails
            ,@RequestBody @Valid DeleteCounseleeConsentReq deleteCounseleeConsentReq) throws RuntimeException {

        DeleteCounseleeConsentRes deleteCounseleeConsentRes = counseleeConsentService.deleteCounseleeConsent(userDetails.getUsername(),deleteCounseleeConsentReq);

        return ResponseEntity.ok(new CommonRes<>(deleteCounseleeConsentRes));

    }







}
