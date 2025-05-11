package com.springboot.api.counselsession.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.counselsession.dto.counseleeconsent.DeleteCounseleeConsentRes;
import com.springboot.api.counselsession.dto.counseleeconsent.SelectCounseleeConsentByCounseleeIdRes;
import com.springboot.api.counselsession.dto.counseleeconsent.UpdateCounseleeConsentRes;
import com.springboot.api.counselsession.service.CounseleeConsentService;
import com.springboot.enums.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@ApiController(path = "/v1/counselee/consent", name = "CounseleeConsentController", description = "내담자 동의 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class CounseleeConsentController {

    private final CounseleeConsentService counseleeConsentService;

    @GetMapping("/{counselSessionId}")
    @Operation(summary = "내담자 개인정보 수집 동의 여부 조회", tags = {"개인 정보 수집 동의"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<SelectCounseleeConsentByCounseleeIdRes>> selectCounseleeConsentByCounseleeId(
        @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId,
        @RequestParam @NotBlank(message = "내담자 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "내담자 ID는 26자여야 합니다") String counseleeId) {

        SelectCounseleeConsentByCounseleeIdRes selectCounseleeConsentByCounseleeIdRes = counseleeConsentService
            .selectCounseleeConsentByCounseleeId(
                counselSessionId, counseleeId);

        return ResponseEntity.ok(new CommonRes<>(selectCounseleeConsentByCounseleeIdRes));
    }
    
    @PutMapping("/{counseleeConsentId}")
    @Operation(summary = "내담자 개인정보 수집 동의", tags = {"개인 정보 수집 동의"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<UpdateCounseleeConsentRes>> acceptCounseleeConsent(
        @PathVariable @NotBlank(message = "내담자 동의 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "내담자 동의 ID는 26자여야 합니다") String counseleeConsentId) {

        UpdateCounseleeConsentRes updateCounseleeConsentRes = counseleeConsentService.acceptCounseleeConsent(
            counseleeConsentId);

        return ResponseEntity.ok(new CommonRes<>(updateCounseleeConsentRes));
    }

    @DeleteMapping("/{counseleeConsentId}")
    @Operation(summary = "내담자 개인정보 수집 동의 여부 삭제", tags = {"개인 정보 수집 동의"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<DeleteCounseleeConsentRes>> deleteCounseleeConsent(
        @PathVariable @NotBlank(message = "내담자 동의 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "내담자 동의 ID는 26자여야 합니다") String counseleeConsentId) {

        DeleteCounseleeConsentRes deleteCounseleeConsentRes = counseleeConsentService
            .deleteCounseleeConsent(counseleeConsentId);

        return ResponseEntity.ok(new CommonRes<>(deleteCounseleeConsentRes));

    }
}
