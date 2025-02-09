package com.springboot.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.medicationRecordHist.AddAndUpdateMedicationRecordHistReq;
import com.springboot.api.dto.medicationRecordHist.AddAndUpdateMedicationRecordHistRes;
import com.springboot.api.dto.medicationRecordHist.SelectMedicationRecordHistRes;
import com.springboot.api.service.MedicationRecordHistService;
import com.springboot.enums.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@ApiController(name = "MedicationRecordHistController", path = "/v1/counsel/medication/record", description = "본상담 내 복약상담 관련 API를 제공하는 Controller")
@RequiredArgsConstructor
public class MedicationRecordHistController {

    private final MedicationRecordHistService medicationRecordHistService;

    @PostMapping("/{counselSessionId}")
    @Operation(summary = "처방 의약품 추가", tags = { "본상담 - 복약 상담" })
    @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    public ResponseEntity<CommonRes<List<AddAndUpdateMedicationRecordHistRes>>> addAndUpdateMedicationRecordHist(
            @PathVariable("counselSessionId") String counselSessionId,
            @RequestBody @Valid List<AddAndUpdateMedicationRecordHistReq> addAndUpdateMedicationRecordHistReqs) {

        List<AddAndUpdateMedicationRecordHistRes> addAndUpdateMedicationRecordHistRes = medicationRecordHistService
                .addAndUpdateMedicationRecordHists(counselSessionId, addAndUpdateMedicationRecordHistReqs);

        return ResponseEntity.ok(new CommonRes<>(addAndUpdateMedicationRecordHistRes));
    }

    // @PostMapping("/{counselSessionId}/batch")
    // @Operation(summary = "처방 의약품 일괄 추가", tags = {"본상담 - 복약 상담"})
    // public ResponseEntity<CommonRes<List<AddMedicationRecordHistRes>>>
    // addMedicationRecordHists(
    // @PathVariable("counselSessionId") String counselSessionId,
    // @RequestBody @Valid List<AddMedicationRecordHistReq>
    // addMedicationRecordHistReqs) {
    // List<AddMedicationRecordHistRes> addMedicationRecordHistRes =
    // medicationRecordHistService
    // .addMedicationRecordHists(counselSessionId, addMedicationRecordHistReqs);
    // return ResponseEntity.ok(new CommonRes<>(addMedicationRecordHistRes));
    // }
    @GetMapping("/{counselSessionId}")
    @Operation(summary = "처방 의약품 리스트 조회", tags = { "본상담 - 복약 상담" })
    @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    public ResponseEntity<CommonRes<List<SelectMedicationRecordHistRes>>> selectMedicationRecordListBySessionId(
            @PathVariable("counselSessionId") String counselSessionId) {

        List<SelectMedicationRecordHistRes> selectMedicationRecordListBySessionIdRes = medicationRecordHistService
                .selectMedicationRecordHistByCounselSessionId(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectMedicationRecordListBySessionIdRes));
    }

    // @PostMapping("/{counselSessionId}/update")
    // @Operation(summary = "처방 의약품 수정", tags = {"본상담 - 복약 상담"})
    // public ResponseEntity<CommonRes<UpdateMedicationRecordHistRes>>
    // updateMedicationRecordHist(
    // @PathVariable("counselSessionId") String counselSessionId,
    // @RequestBody @Valid UpdateMedicationRecordHistReq
    // updateMedicationRecordHistReq) {
    // UpdateMedicationRecordHistRes updateMedicationRecordHistRes =
    // medicationRecordHistService
    // .updateMedicationRecordHist(counselSessionId, updateMedicationRecordHistReq);
    // return ResponseEntity.ok(new CommonRes<>(updateMedicationRecordHistRes));
    // }
    @DeleteMapping("/{counselSessionId}/{id}")
    @Operation(summary = "처방 의약품 삭제", tags = { "본상담 - 복약 상담" })
    @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    public ResponseEntity<CommonRes<Void>> deleteMedicationRecordHist(
            @PathVariable("counselSessionId") String counselSessionId,
            @PathVariable("id") String id) {

        medicationRecordHistService.deleteMedicationRecordHist(counselSessionId, id);

        return ResponseEntity.ok(new CommonRes<>(null));
    }

    @DeleteMapping("/{counselSessionId}")
    @Operation(summary = "처방 의약품 일괄 삭제", tags = { "본상담 - 복약 상담" })
    @RoleSecured({ RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    public ResponseEntity<CommonRes<Void>> deleteMedicationRecordHistsByCounselSessionId(
            @PathVariable("counselSessionId") String counselSessionId) {

        medicationRecordHistService.deleteMedicationRecordHistByCounselSessionId(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(null));
    }

}
