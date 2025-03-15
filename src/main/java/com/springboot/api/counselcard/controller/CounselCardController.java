package com.springboot.api.counselcard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.counselcard.dto.AddCounselCardReq;
import com.springboot.api.counselcard.dto.AddCounselCardRes;
import com.springboot.api.counselcard.dto.DeleteCounselCardReq;
import com.springboot.api.counselcard.dto.DeleteCounselCardRes;
import com.springboot.api.counselcard.dto.SelectCounselCardRes;
import com.springboot.api.counselcard.dto.SelectPreviousCounselCardRes;
import com.springboot.api.counselcard.dto.SelectPreviousItemListByInformationNameAndItemNameRes;
import com.springboot.api.counselcard.dto.UpdateCounselCardReq;
import com.springboot.api.counselcard.dto.UpdateCounselCardRes;
import com.springboot.api.counselcard.service.CounselCardService;
import com.springboot.enums.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@ApiController(path = "/v1/counsel/card", name = "CounselCardController", description = "상담카드 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class CounselCardController {

    private final CounselCardService counselCardService;

    @GetMapping("/{counselSessionId}")
    @Operation(summary = "상담 카드 조회", tags = { "상담 카드 작성", "본상담 - 상담 카드" })
    @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    ResponseEntity<CommonRes<SelectCounselCardRes>> selectCounselCard(
            @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId)
            throws JsonProcessingException {

        SelectCounselCardRes selectCounselCardRes = counselCardService.selectCounselCard(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectCounselCardRes));
    }

    @GetMapping("/{counselSessionId}/previous")
    @Operation(summary = "이전 상담 카드 조회", tags = { "상담 카드 작성" })
    @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    ResponseEntity<CommonRes<SelectPreviousCounselCardRes>> selectPreviousCounselCard(
            @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId)
            throws JsonProcessingException {

        SelectPreviousCounselCardRes selectPreviousCounselCardRes = counselCardService
                .selectPreviousCounselCard(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectPreviousCounselCardRes));
    }

    @PostMapping
    @Operation(summary = "상담 카드 등록", tags = { "상담 카드 작성" })
    @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    ResponseEntity<CommonRes<AddCounselCardRes>> addCounselCard(
            @RequestBody @Valid AddCounselCardReq addCounselCardReq) {

        AddCounselCardRes addCounselCardRes = counselCardService.addCounselCard(addCounselCardReq);

        return ResponseEntity.ok(new CommonRes<>(addCounselCardRes));
    }

    @PutMapping
    @Operation(summary = "상담 카드 수정", tags = { "상담 카드 작성" })
    @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    ResponseEntity<CommonRes<UpdateCounselCardRes>> updateCounselCard(
            @RequestBody @Valid UpdateCounselCardReq updateCounselCardReq) {

        UpdateCounselCardRes updateCounselCardRes = counselCardService.updateCounselCard(updateCounselCardReq);

        return ResponseEntity.ok(new CommonRes<>(updateCounselCardRes));
    }

    @DeleteMapping
    @Operation(summary = "상담 카드 삭제")
    @RoleSecured(RoleType.ROLE_ADMIN)
    ResponseEntity<CommonRes<DeleteCounselCardRes>> deleteCounselCard(
            @RequestBody @Valid DeleteCounselCardReq deleteCounselCardReq) {

        DeleteCounselCardRes deleteCounselCardRes = counselCardService.deleteCounselCard(deleteCounselCardReq);

        return ResponseEntity.ok(new CommonRes<>(deleteCounselCardRes));
    }

    @GetMapping("/{counselSessionId}/preious/item/list")
    @Operation(summary = "이전 상담 카드 item 목록 조회", tags = { "본상담 - 상담 카드" })
    @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    ResponseEntity<CommonRes<List<SelectPreviousItemListByInformationNameAndItemNameRes>>> selectPreviousItemListByInformationNameAndItemName(
            @PathVariable @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId,
            @RequestParam(required = true) String informationName,
            @RequestParam(required = true) String itemName) {

        List<SelectPreviousItemListByInformationNameAndItemNameRes> selectPreviousItemListResListByInformationNameAndInformationItemName = counselCardService
                .selectPreviousItemListByInformationNameAndItemName(
                        counselSessionId, informationName, itemName);

        return ResponseEntity.ok(new CommonRes<>(selectPreviousItemListResListByInformationNameAndInformationItemName));
    }

}
