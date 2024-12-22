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
    @Operation(summary = "복약 상담 추가",tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<AddMedicationCounselRes>> addMedicationCounsel(@AuthenticationPrincipal UserDetails userDetails
            , @RequestBody @Valid AddMedicationCounselReq addMedicationCounselReq) {

        AddMedicationCounselRes addMedicationCounselRes = medicationCounselService.addMedicationCounsel(userDetails.getUsername(), addMedicationCounselReq);

        return ResponseEntity.ok(new CommonRes<>(addMedicationCounselRes));

    }

    @GetMapping
    @Operation(summary = "복약 상담 조회", tags ={"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<SelectMedicationCounselRes>> selectMedicationCounsel(
            @AuthenticationPrincipal UserDetails userDetails
            , @RequestParam("counselSessionId") String counselSessionId) {

        SelectMedicationCounselRes selectMedicationCounselRes = medicationCounselService
                .selectMedicationCounsel(userDetails.getUsername(), counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectMedicationCounselRes));

    }


    @GetMapping("/{counselSessionId}/previous/summary")
    @Operation(summary = "이전 복약 상담 조회", tags ={"본상담 - 이전 상담 내역"})
    public ResponseEntity<CommonRes<SelectPreviousMedicationCounselRes>> selectPreviousMedicationCounsel(
            @AuthenticationPrincipal UserDetails userDetails
            , @PathVariable("counselSessionId") String counselSessionId) {

        SelectPreviousMedicationCounselRes selectPreviousMedicationCounselRes = medicationCounselService
                .selectPreviousMedicationCounsel(userDetails.getUsername(), counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectPreviousMedicationCounselRes));

    }




    @PutMapping
    @Operation(summary = "복약 상담 수정", tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<UpdateMedicationCounselRes>> updateMedicationCounsel(@AuthenticationPrincipal UserDetails userDetails
    , @RequestBody @Valid UpdateMedicationCounselReq updateMedicationCounselReq) {

        UpdateMedicationCounselRes updateMedicationCounselRes = medicationCounselService.updateMedicationCounsel(userDetails.getUsername(), updateMedicationCounselReq);

        return ResponseEntity.ok(new CommonRes<>(updateMedicationCounselRes));

    }


    @DeleteMapping
    @Operation(summary = "복약 상담 삭제", tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<DeleteMedicationCounselRes>> deleteMedicationCounsel(@AuthenticationPrincipal UserDetails userDetails
    , @RequestBody @Valid DeleteMedicationCounselReq deleteMedicationCounselReq){

        DeleteMedicationCounselRes deleteMedicationCounselRes = medicationCounselService.deleteMedicationCounsel(userDetails.getUsername(), deleteMedicationCounselReq);

        return ResponseEntity.ok(new CommonRes<>(deleteMedicationCounselRes));

    }











}
