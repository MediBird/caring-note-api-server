package com.springboot.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.wasteMedication.AddWasteMedicationRecordReq;
import com.springboot.api.dto.wasteMedication.AddWasteMedicationRecordRes;
import com.springboot.api.dto.wasteMedication.SelectMedicationRecordListBySessionIdReq;
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

    @PostMapping("/single")
    @Operation(summary = "폐의약품 추가", tags = {"본상담 - 폐의약품 목록"})
    public ResponseEntity<CommonRes<AddWasteMedicationRecordRes>> addWasteMedicationRecord(
            @RequestBody @Valid AddWasteMedicationRecordReq addWasteMedicationRecordReq) {

        AddWasteMedicationRecordRes addWasteMedicationRecordRes = wasteMedicationRecordService
                .createWasteMedicationRecord(addWasteMedicationRecordReq);

        return ResponseEntity.ok(new CommonRes<>(addWasteMedicationRecordRes));
    }

    @PostMapping("/batch")
    @Operation(summary = "폐의약품 리스트 추가", tags = {"본상담 - 폐의약품 목록"})
    public ResponseEntity<CommonRes<List<AddWasteMedicationRecordRes>>> addWasteMedicationRecords(
            @RequestBody @Valid List<AddWasteMedicationRecordReq> addWasteMedicationRecordReqs) {

        List<AddWasteMedicationRecordRes> addWasteMedicationRecordResList = wasteMedicationRecordService
                .createWasteMedicationRecords(addWasteMedicationRecordReqs);

        return ResponseEntity.ok(new CommonRes<>(addWasteMedicationRecordResList));
    }

    // @PostMapping("/update")
    // @Operation(summary = "폐의약품 수정", tags = {"본상담 - 폐의약품 목록"})
    // public ResponseEntity<CommonRes<UpdateWasteMedicationRecordRes>> updateWasteMedicationRecord(
    //         @RequestBody @Valid UpdateWasteMedicationRecordReq updateWasteMedicationRecordReq) {
    // }
    // @PostMapping("/delete")
    // @Operation(summary = "폐의약품 삭제", tags = {"본상담 - 폐의약품 목록"})
    // public ResponseEntity<CommonRes<DeleteWasteMedicationRecordRes>> deleteWasteMedicationRecord(
    //         @RequestBody @Valid DeleteWasteMedicationRecordReq deleteWasteMedicationRecordReq) {
    // }
    @PostMapping("/list")
    @Operation(summary = "폐의약품 리스트 조회", tags = {"본상담 - 폐의약품 목록"})
    public ResponseEntity<CommonRes<SelectMedicationRecordListBySessionIdRes>> selectMedicationRecordListBySessionId(
            @RequestBody @Valid SelectMedicationRecordListBySessionIdReq selectMedicationRecordListBySessionIdReq) {

        SelectMedicationRecordListBySessionIdRes selectMedicationRecordListBySessionIdRes = wasteMedicationRecordService
                .getWasteMedicationRecord(selectMedicationRecordListBySessionIdReq.getCounselSessionId());

        return ResponseEntity.ok(new CommonRes<>(selectMedicationRecordListBySessionIdRes));

    }
}
