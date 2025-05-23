package com.springboot.api.counselsession.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.counselsession.dto.wasteMedication.AddAndUpdateWasteMedicationRecordReq;
import com.springboot.api.counselsession.dto.wasteMedication.AddAndUpdateWasteMedicationRecordRes;
import com.springboot.api.counselsession.dto.wasteMedication.SelectMedicationRecordListBySessionIdRes;
import com.springboot.api.counselsession.dto.wasteMedicationDisposal.WasteMedicationDisposalReq;
import com.springboot.api.counselsession.dto.wasteMedicationDisposal.WasteMedicationDisposalRes;
import com.springboot.api.counselsession.entity.WasteMedicationDisposal;
import com.springboot.api.counselsession.service.WasteMedicationDisposalService;
import com.springboot.api.counselsession.service.WasteMedicationRecordService;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiController(name = "WasteMedicationController", path = "/v1/counsel/medication/waste", description = "본상담 내 복약상담 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class WasteMedicationController {

    private final WasteMedicationRecordService wasteMedicationRecordService;
    private final WasteMedicationDisposalService wasteMedicationDisposalService;

    @PostMapping("/{counselSessionId}")
    @Operation(summary = "폐의약품 추가 및 업데이트", tags = {"본상담 - 폐의약품 목록"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<List<AddAndUpdateWasteMedicationRecordRes>>> addAndUpdateWasteMedicationRecord(
        @PathVariable("counselSessionId") String counselSessionId,
        @RequestBody @Valid List<AddAndUpdateWasteMedicationRecordReq> addAndUpdateWasteMedicationRecordReqs) {

        List<AddAndUpdateWasteMedicationRecordRes> addAndUpdateWasteMedicationRecordRes = wasteMedicationRecordService
            .addAndUpdateWasteMedicationRecords(counselSessionId, addAndUpdateWasteMedicationRecordReqs);

        return ResponseEntity.ok(new CommonRes<>(addAndUpdateWasteMedicationRecordRes));
    }

    @GetMapping("/{counselSessionId}")
    @Operation(summary = "폐의약품 리스트 조회", tags = {"본상담 - 폐의약품 목록"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<List<SelectMedicationRecordListBySessionIdRes>>> selectMedicationRecordListBySessionId(
        @PathVariable("counselSessionId") String counselSessionId) {

        List<SelectMedicationRecordListBySessionIdRes> selectMedicationRecordListBySessionIdRes = wasteMedicationRecordService
            .getWasteMedicationRecord(counselSessionId);
        return ResponseEntity.ok(new CommonRes<>(selectMedicationRecordListBySessionIdRes));
    }

    @DeleteMapping("/{counselSessionId}/{wasteMedicationRecordId}")
    @Operation(summary = "폐의약품 삭제", tags = {"본상담 - 폐의약품 목록"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<Void>> deleteWasteMedicationRecord(
        @PathVariable("counselSessionId") String counselSessionId,
        @PathVariable("wasteMedicationRecordId") String wasteMedicationRecordId) {

        wasteMedicationRecordService.deleteWasteMedicationRecord(counselSessionId, wasteMedicationRecordId);
        return ResponseEntity.ok(new CommonRes<>(null));
    }

    @PostMapping("/disposal/{counselSessionId}")
    @Operation(summary = "폐의약품 폐기 정보 추가 혹은 업데이트", tags = {"본상담 - 폐의약품 목록"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<String>> addWasteMedicationDisposal(
        @PathVariable("counselSessionId") String counselSessionId,
        @RequestBody @Valid WasteMedicationDisposalReq wasteMedicationDisposalReq) {

        return ResponseEntity.ok(new CommonRes<>(
            wasteMedicationDisposalService.save(counselSessionId, wasteMedicationDisposalReq)));
    }

    @DeleteMapping("/disposal/{counselSessionId}")
    @Operation(summary = "폐의약품 폐기 정보 삭제", tags = {"본상담 - 폐의약품 목록"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<Void>> deleteWasteMedicationDisposal(
        @PathVariable("counselSessionId") String counselSessionId) {
        wasteMedicationDisposalService.delete(counselSessionId);
        return ResponseEntity.ok(new CommonRes<>(null));
    }

    @GetMapping("/disposal/{counselSessionId}")
    @Operation(summary = "폐의약품 폐기 정보 조회", tags = {"본상담 - 폐의약품 목록"})
    @RoleSecured({RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    public ResponseEntity<CommonRes<WasteMedicationDisposalRes>> getWasteMedicationDisposal(
        @PathVariable("counselSessionId") String counselSessionId) {
        WasteMedicationDisposal disposal = wasteMedicationDisposalService.get(counselSessionId);
        WasteMedicationDisposalRes response = new WasteMedicationDisposalRes(disposal);
        return ResponseEntity.ok(new CommonRes<>(response));

    }

}
