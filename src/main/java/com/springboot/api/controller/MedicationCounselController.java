package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.medicationcounsel.*;
import com.springboot.api.service.MedicationCounselService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@ApiController(
        name = "MedicationCounselController"
        ,path ="/v1/counsel/record"
        ,description ="본상담 내 복약상담 관련 API를 제공하는 Controller"
)
@RequiredArgsConstructor
public class MedicationCounselController {

    private final MedicationCounselService medicationCounselService;


    @PostMapping
    @Operation(summary = "복약 상담 추가",tags = {"복약 상담 화면"})
    public ResponseEntity<CommonRes<AddRes>> add(@AuthenticationPrincipal UserDetails userDetails
            , @RequestBody @Valid AddReq addReq) {

        AddRes addRes = medicationCounselService.add(userDetails.getUsername(),addReq);

        return ResponseEntity.ok(new CommonRes<>(addRes));

    }

    @GetMapping
    @Operation(summary = "복약 상담 조회", tags ={"복약 상담 화면"})
    public ResponseEntity<CommonRes<SelectByCounselSessionIdRes>> selectByCounselSessionId(
            @AuthenticationPrincipal UserDetails userDetails
            , @RequestParam("counselSessionId") String counselSessionId) {

        SelectByCounselSessionIdRes selectByCounselSessionIdRes = medicationCounselService
                .selectByCounselSessionId(userDetails.getUsername(), counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectByCounselSessionIdRes));

    }


    @GetMapping("/{counselSessionId}/previous/summary")
    @Operation(summary = "이전 복약 상담 조회", tags ={"이전 상담 내역"})
    public ResponseEntity<CommonRes<SelectPreviousByCounselSessionIdRes>> selectPreviousByCounselSessionId(
            @AuthenticationPrincipal UserDetails userDetails
            , @PathVariable("counselSessionId") String counselSessionId) {

        SelectPreviousByCounselSessionIdRes selectPreviousByCounselSessionIdRes = medicationCounselService
                .selectPreviousByCounselSessionId(userDetails.getUsername(), counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectPreviousByCounselSessionIdRes));

    }




    @PutMapping
    @Operation(summary = "복약 상담 수정", tags = {"복약 상담 화면"})
    public ResponseEntity<CommonRes<UpdateRes>> update(@AuthenticationPrincipal UserDetails userDetails
    , @RequestBody @Valid UpdateReq updateReq) {

        UpdateRes updateRes = medicationCounselService.update(userDetails.getUsername(),updateReq);

        return ResponseEntity.ok(new CommonRes<>(updateRes));

    }


    @DeleteMapping
    @Operation(summary = "복약 상담 삭제", tags = {"복약 상담 화면"})
    public ResponseEntity<CommonRes<DeleteRes>> delete(@AuthenticationPrincipal UserDetails userDetails
    ,@RequestBody @Valid DeleteReq deleteReq){

        DeleteRes deleteRes = medicationCounselService.delete(userDetails.getUsername(),deleteReq);

        return ResponseEntity.ok(new CommonRes<>(deleteRes));

    }











}
