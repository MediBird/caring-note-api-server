package com.springboot.api.counselcard.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.counselcard.dto.request.UpdateBaseInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateHealthInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateIndependentLifeInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateLivingInformationReq;
import com.springboot.api.counselcard.dto.response.CounselCardIdRes;
import com.springboot.api.counselcard.dto.response.CounselCardRes;
import com.springboot.api.counselcard.dto.response.TimeRecordedRes;
import com.springboot.api.counselcard.service.CounselCardService;

import com.springboot.enums.CounselCardRecordType;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@ApiController(path = "/v1/counsel/card", name = "CounselCardController", description = "상담카드 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class CounselCardController {

    private final CounselCardService counselCardService;

    @GetMapping("/{counselSessionId}")
    @Operation(summary = "상담 카드 조회", tags = {"상담 카드 작성", "본상담 - 상담 카드"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    ResponseEntity<CommonRes<CounselCardRes>> selectCounselCard(
        @PathVariable @NotBlank(message = "내담자 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "내담자 ID는 26자여야 합니다") String counselSessionId) {
        return ResponseEntity.ok(
            new CommonRes<>(counselCardService.selectCounselCard(counselSessionId)));
    }

    @PutMapping("/{counselSessionId}/base-information")
    @Operation(summary = "상담 카드 기본 정보 수정", tags = {"상담 카드 작성"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    ResponseEntity<CommonRes<CounselCardIdRes>> updateCounselCardBaseInformation(
        @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId,
        @RequestBody @Valid UpdateBaseInformationReq updateBaseInformationReq) {

        return ResponseEntity.ok(
            new CommonRes<>(counselCardService.updateCounselCardBaseInformation(counselSessionId, updateBaseInformationReq)));
    }

    @PutMapping("/{counselSessionId}/health-information")
    @Operation(summary = "상담 카드 건강 정보 수정", tags = {"상담 카드 작성"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    ResponseEntity<CommonRes<CounselCardIdRes>> updateCounselCardHealthInformation(
        @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId,
        @RequestBody @Valid UpdateHealthInformationReq updateHealthInformationReq) {

        return ResponseEntity.ok(
            new CommonRes<>(counselCardService.updateCounselCardHealthInformation(counselSessionId, updateHealthInformationReq)));
    }

    @PutMapping("/{counselSessionId}/living-information")
    @Operation(summary = "상담 카드 생활 정보 수정", tags = {"상담 카드 작성"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    ResponseEntity<CommonRes<CounselCardIdRes>> updateCounselCardLivingInformation(
        @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId,
        @RequestBody @Valid UpdateLivingInformationReq updateLivingInformationReq) {

        return ResponseEntity.ok(
            new CommonRes<>(counselCardService.updateCounselCardLivingInformation(counselSessionId, updateLivingInformationReq)));
    }

    @PutMapping("/{counselSessionId}/independent-life-information")
    @Operation(summary = "상담 카드 자립생활 역량 수정", tags = {"상담 카드 작성"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    ResponseEntity<CommonRes<CounselCardIdRes>> updateCounselCardIndependentLifeInformation(
        @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId,
        @RequestBody @Valid UpdateIndependentLifeInformationReq independentLifeInformationReq) {

        return ResponseEntity.ok(
            new CommonRes<>(counselCardService.updateCounselCardIndependentLifeInformation(counselSessionId, independentLifeInformationReq)));
    }

    @DeleteMapping("/{counselSessionId}")
    @Operation(summary = "상담 카드 삭제")
    @RoleSecured(RoleType.ROLE_ADMIN)
    ResponseEntity<CommonRes<CounselCardIdRes>> deleteCounselCard(
        @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId) {

        return ResponseEntity.ok(
            new CommonRes<>(counselCardService.deleteCounselCard(counselSessionId)));
    }

    @GetMapping("/{counselSessionId}/preious/item/list")
    @Operation(summary = "이전 상담 카드 item 목록 조회", tags = {"본상담 - 상담 카드"})
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    ResponseEntity<CommonRes<List<TimeRecordedRes<Object>>>> selectPreviousItemListByInformationNameAndItemName(
        @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId,
        @RequestParam @Valid @ValidEnum(enumClass = CounselCardRecordType.class) CounselCardRecordType type) {

        return ResponseEntity.ok(
            new CommonRes<>(
                counselCardService.selectPreviousRecordsByType(counselSessionId, type)));
    }
}
