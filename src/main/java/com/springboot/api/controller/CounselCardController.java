package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counselcard.*;
import com.springboot.api.service.CounselCardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@ApiController(
        path="/v1/counsel/card"
        ,name = "CounselCardController"
        , description = "상담카드 관련 API를 제공하는 Controller"
)
public class CounselCardController {

    private final CounselCardService counselCardService;

    public CounselCardController(CounselCardService counselCardService) {
        this.counselCardService = counselCardService;
    }

    @GetMapping("/{counselSessionId}")
    @Operation(summary = "상담 카드 조회",tags = {"상담 카드 작성"})
    ResponseEntity<CommonRes<SelectCounselCardRes>> selectCounselCard(@AuthenticationPrincipal UserDetails userDetails
            , @PathVariable String counselSessionId) {

        SelectCounselCardRes selectCounselCardRes = counselCardService.selectCounselCard(userDetails.getUsername(), counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectCounselCardRes));
    }

    @GetMapping("previous/{counselSessionId}")
    @Operation(summary = "이전 상담 카드 조회",tags = {"상담 카드 작성"})
    ResponseEntity<CommonRes<SelectPreviousCounselCardRes>> selectPreviousCounselCard(@AuthenticationPrincipal UserDetails userDetails
            , @PathVariable String counselSessionId) {

        SelectPreviousCounselCardRes selectPreviousCounselCardRes = counselCardService.selectPreviousCounselCard(userDetails.getUsername(), counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectPreviousCounselCardRes));
    }


    @PostMapping
    @Operation(summary = "상담 카드 등록",tags = {"상담 카드 작성"})
    ResponseEntity<CommonRes<AddCounselCardRes>> addCounselCard(@AuthenticationPrincipal UserDetails userDetails
                                                                    , @RequestBody @Valid AddCounselCardReq addCounselCardReq) {

        AddCounselCardRes addCounselCardRes = counselCardService.addCounselCard(userDetails.getUsername(), addCounselCardReq);


        return ResponseEntity.ok(new CommonRes<>(addCounselCardRes));
    }

    @PutMapping
    @Operation(summary = "상담 카드 수정",tags = {"상담 카드 작성"})
    ResponseEntity<CommonRes<UpdateCounselCardRes>> updateCounselCard(@AuthenticationPrincipal UserDetails userDetails
            , @RequestBody @Valid UpdateCounselCardReq updateCounselCardReq) {

        UpdateCounselCardRes updateCounselCardRes = counselCardService.updateCounselCard(userDetails.getUsername(), updateCounselCardReq);


        return ResponseEntity.ok(new CommonRes<>(updateCounselCardRes));
    }

    @DeleteMapping
    @Operation(summary = "상담 카드 삭제")
    ResponseEntity<CommonRes<DeleteCounselCardRes>> deleteCounselCard(@AuthenticationPrincipal UserDetails userDetails
            , @RequestBody @Valid DeleteCounselCardReq deleteCounselCardReq) {

        DeleteCounselCardRes deleteCounselCardRes = counselCardService.deleteCounselCard(userDetails.getUsername(), deleteCounselCardReq);

        return ResponseEntity.ok(new CommonRes<>(deleteCounselCardRes));
    }



}
