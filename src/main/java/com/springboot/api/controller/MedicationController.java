package com.springboot.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.medication.MedicationAutoCompleteDTO;
import com.springboot.api.service.MedicationService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApiController(
        name = "MedicationController"
        ,description = "약 관련 API를 제공하는 Controller"
        ,path = "/v1/medication"
)
@RequiredArgsConstructor
@Slf4j
public class MedicationController {
    private final MedicationService medicationService;

    @Operation(summary = "약 검색",tags = {"약 검색"})
    @GetMapping
    public ResponseEntity<CommonRes<List<MedicationAutoCompleteDTO>>> searchMedication(@RequestParam String keyword) {
        List<MedicationAutoCompleteDTO> medications = medicationService.searchMedicationsByName(keyword);
        log.info("medications: {}", medications);
        return ResponseEntity.ok(new CommonRes<>(medications));
    }
}