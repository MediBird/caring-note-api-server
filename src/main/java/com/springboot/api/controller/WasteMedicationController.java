package com.springboot.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.wasteMedication.AddAndUpdateWasteMedicationRecordReq;
import com.springboot.api.dto.wasteMedication.AddAndUpdateWasteMedicationRecordRes;
import com.springboot.api.dto.wasteMedication.SelectMedicationRecordListBySessionIdRes;
import com.springboot.api.service.WasteMedicationRecordService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@ApiController(
        name = "WasteMedicationController",
        path = "/v1/counsel/medication/waste",
        description = "본상담 내 복약상담 관련 API를 제공하는 Controller"
)
@RequiredArgsConstructor
public class WasteMedicationController {

    private final WasteMedicationRecordService wasteMedicationRecordService;

    @PostMapping("/{counselSessionId}")
    @Operation(summary = "폐의약품 추가", tags = {"본상담 - 폐의약품 목록"})
    public ResponseEntity<CommonRes<List<AddAndUpdateWasteMedicationRecordRes>>> addWasteMedicationRecord(
            @PathVariable("counselSessionId") String counselSessionId,
            @RequestBody @Valid List<AddAndUpdateWasteMedicationRecordReq> addAndUpdateWasteMedicationRecordReqs) {

        List<AddAndUpdateWasteMedicationRecordRes> addAndUpdateWasteMedicationRecordRes = wasteMedicationRecordService
                .addAndUpdateWasteMedicationRecords(counselSessionId, addAndUpdateWasteMedicationRecordReqs);

        return ResponseEntity.ok(new CommonRes<>(addAndUpdateWasteMedicationRecordRes));
    }

    @GetMapping("/{counselSessionId}")
    @Operation(summary = "폐의약품 리스트 조회", tags = {"본상담 - 폐의약품 목록"})
    public ResponseEntity<CommonRes<List<SelectMedicationRecordListBySessionIdRes>>> selectMedicationRecordListBySessionId(
            @PathVariable("counselSessionId") String counselSessionId) {

        List<SelectMedicationRecordListBySessionIdRes> selectMedicationRecordListBySessionIdRes = wasteMedicationRecordService
                .getWasteMedicationRecord(counselSessionId);
        return ResponseEntity.ok(new CommonRes<>(selectMedicationRecordListBySessionIdRes));
    }

    @DeleteMapping("/{counselSessionId}/{wasteMedicationRecordId}")
    @Operation(summary = "폐의약품 삭제", tags = {"본상담 - 폐의약품 목록"})
    public ResponseEntity<CommonRes<Void>> deleteWasteMedicationRecord(
            @PathVariable("counselSessionId") String counselSessionId,
            @PathVariable("wasteMedicationRecordId") String wasteMedicationRecordId) {

        wasteMedicationRecordService.deleteWasteMedicationRecord(counselSessionId, wasteMedicationRecordId);
        return ResponseEntity.ok(new CommonRes<>(null));
    }
}
