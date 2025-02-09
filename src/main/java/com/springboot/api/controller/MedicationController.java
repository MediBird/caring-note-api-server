package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.medication.SearchMedicationByKeywordRes;
import com.springboot.api.service.MedicationService;
import com.springboot.enums.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@ApiController(name = "MedicationController", description = "약 관련 API를 제공하는 Controller", path = "/v1/medication")
@RequiredArgsConstructor
@Slf4j
public class MedicationController {
    private final MedicationService medicationService;

    @Operation(summary = "약 검색", tags = { "약 검색" })
    @GetMapping
    @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
    public ResponseEntity<CommonRes<List<SearchMedicationByKeywordRes>>> searchMedicationByKeyword(
            @RequestParam String keyword) {
        List<SearchMedicationByKeywordRes> medications = medicationService.searchMedicationByKeyword(keyword);
        log.info("medications: {}", medications);
        return ResponseEntity.ok(new CommonRes<>(medications));
    }
}