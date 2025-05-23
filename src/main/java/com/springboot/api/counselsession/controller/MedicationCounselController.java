package com.springboot.api.counselsession.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.counselsession.dto.medicationcounsel.AddMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.AddMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.DeleteMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.DeleteMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.SelectMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.SelectPreviousMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.UpdateMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.UpdateMedicationCounselRes;
import com.springboot.api.counselsession.service.MedicationCounselService;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@ApiController(name = "MedicationCounselController", path = "/v1/counsel/record", description = "본상담 내 복약상담 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class MedicationCounselController {

    private final MedicationCounselService medicationCounselService;

    @PostMapping
    @Operation(summary = "복약 상담 추가", tags = {"본상담 - 복약 상담"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<AddMedicationCounselRes>> addMedicationCounsel(
        @RequestBody @Valid AddMedicationCounselReq addMedicationCounselReq) {

        AddMedicationCounselRes addMedicationCounselRes = medicationCounselService
            .addMedicationCounsel(addMedicationCounselReq);

        return ResponseEntity.ok(new CommonRes<>(addMedicationCounselRes));

    }

    @GetMapping
    @Operation(summary = "복약 상담 조회", tags = {"본상담 - 복약 상담"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<SelectMedicationCounselRes>> selectMedicationCounsel(
        @RequestParam("counselSessionId") String counselSessionId) {

        SelectMedicationCounselRes selectMedicationCounselRes = medicationCounselService
            .selectMedicationCounsel(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectMedicationCounselRes));

    }

    @GetMapping("/{counselSessionId}/previous/summary")
    @Operation(summary = "이전 복약 상담 조회", tags = {"본상담 - 이전 상담 내역"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<SelectPreviousMedicationCounselRes>> selectPreviousMedicationCounsel(
        @PathVariable("counselSessionId") String counselSessionId) {

        SelectPreviousMedicationCounselRes selectPreviousMedicationCounselRes = medicationCounselService
            .selectPreviousMedicationCounsel(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectPreviousMedicationCounselRes));

    }

    @PutMapping
    @Operation(summary = "복약 상담 수정", tags = {"본상담 - 복약 상담"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<UpdateMedicationCounselRes>> updateMedicationCounsel(
        @RequestBody @Valid UpdateMedicationCounselReq updateMedicationCounselReq) {

        UpdateMedicationCounselRes updateMedicationCounselRes = medicationCounselService
            .updateMedicationCounsel(updateMedicationCounselReq);

        return ResponseEntity.ok(new CommonRes<>(updateMedicationCounselRes));

    }

    @DeleteMapping
    @Operation(summary = "복약 상담 삭제", tags = {"본상담 - 복약 상담"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<DeleteMedicationCounselRes>> deleteMedicationCounsel(
        @RequestBody @Valid DeleteMedicationCounselReq deleteMedicationCounselReq) {

        DeleteMedicationCounselRes deleteMedicationCounselRes = medicationCounselService
            .deleteMedicationCounsel(deleteMedicationCounselReq);

        return ResponseEntity.ok(new CommonRes<>(deleteMedicationCounselRes));

    }

}
