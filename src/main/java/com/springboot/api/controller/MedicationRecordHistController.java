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
import com.springboot.api.domain.MedicationRecordHist;
import com.springboot.api.dto.medicationRecordHist.AddMedicationRecordHistReq;
import com.springboot.api.dto.medicationRecordHist.AddMedicationRecordHistRes;
import com.springboot.api.dto.medicationRecordHist.UpdateMedicationRecordHistReq;
import com.springboot.api.dto.medicationRecordHist.UpdateMedicationRecordHistRes;
import com.springboot.api.service.MedicationRecordHistService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@ApiController(
        name = "MedicationRecordHistController",
        path = "/v1/counsel/medication/record",
        description = "본상담 내 복약상담 관련 API를 제공하는 Controller"
)
@RequiredArgsConstructor
public class MedicationRecordHistController {

    private final MedicationRecordHistService medicationRecordHistService;

    @PostMapping
    @Operation(summary = "처방 의약품 추가", tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<AddMedicationRecordHistRes>> addMedicationRecordHist(
            @RequestBody @Valid AddMedicationRecordHistReq addMedicationRecordHistReq) {

        AddMedicationRecordHistRes addMedicationRecordHistRes = medicationRecordHistService
                .addMedicationRecordHist(addMedicationRecordHistReq);

        return ResponseEntity.ok(new CommonRes<>(addMedicationRecordHistRes));
    }

    @PostMapping("/batch")
    @Operation(summary = "처방 의약품 일괄 추가", tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<List<AddMedicationRecordHistRes>>> addMedicationRecordHists(
            @RequestBody @Valid List<AddMedicationRecordHistReq> addMedicationRecordHistReqs) {

        List<AddMedicationRecordHistRes> addMedicationRecordHistRes = medicationRecordHistService
                .addMedicationRecordHists(addMedicationRecordHistReqs);

        return ResponseEntity.ok(new CommonRes<>(addMedicationRecordHistRes));
    }

    @GetMapping("/list/{counselSessionId}")
    @Operation(summary = "처방 의약품 리스트 조회", tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<List<MedicationRecordHist>>> selectMedicationRecordListBySessionId(
            @PathVariable @Valid String counselSessionId) {

        List<MedicationRecordHist> selectMedicationRecordListBySessionIdRes = medicationRecordHistService
                .selectMedicationRecordHistByCounselSessionId(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectMedicationRecordListBySessionIdRes));
    }

    @PostMapping("/update")
    @Operation(summary = "처방 의약품 수정", tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<UpdateMedicationRecordHistRes>> updateMedicationRecordHist(
            @RequestBody @Valid UpdateMedicationRecordHistReq updateMedicationRecordHistReq) {

        UpdateMedicationRecordHistRes updateMedicationRecordHistRes = medicationRecordHistService
                .updateMedicationRecordHist(updateMedicationRecordHistReq);

        return ResponseEntity.ok(new CommonRes<>(updateMedicationRecordHistRes));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "처방 의약품 삭제", tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<Void>> deleteMedicationRecordHist(
            @PathVariable @Valid String id) {

        medicationRecordHistService.deleteMedicationRecordHist(id);

        return ResponseEntity.ok(new CommonRes<>(null));
    }

    @DeleteMapping("/batch/{counselSessionId}")
    @Operation(summary = "처방 의약품 일괄 삭제", tags = {"본상담 - 복약 상담"})
    public ResponseEntity<CommonRes<Void>> deleteMedicationRecordHistsByCounselSessionId(
            @PathVariable @Valid String counselSessionId) {

        medicationRecordHistService.deleteMedicationRecordHistByCounselSessionId(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(null));
    }

}
