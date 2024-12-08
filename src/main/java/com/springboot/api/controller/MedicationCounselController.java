package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.medicationcounsel.AddReq;
import com.springboot.api.dto.medicationcounsel.AddRes;
import com.springboot.api.service.MedicationCounselService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiController(
        name = "MedicationCounselController"
        ,path ="/v1/counsel/record"
        ,description ="본상담 내 복약상담 관련 API를 제공하는 Controller"
)
public class MedicationCounselController {

    private final MedicationCounselService medicationCounselService;

    public MedicationCounselController(MedicationCounselService medicationCounselService) {
        this.medicationCounselService = medicationCounselService;
    }

    @PostMapping
    @Operation(summary = "복약 상담 추가",tags = {"복약 상담 화면"})
    public ResponseEntity<CommonRes<AddRes>> add(@AuthenticationPrincipal UserDetails userDetails
            , @RequestBody @Valid AddReq addReq) throws RuntimeException {

        AddRes addRes = medicationCounselService.add(userDetails.getUsername(),addReq);

        return ResponseEntity.ok(new CommonRes<>(addRes));

    }











}
