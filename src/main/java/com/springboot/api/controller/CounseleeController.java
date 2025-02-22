package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.common.message.HttpMessages;
import com.springboot.api.dto.counselee.*;
import com.springboot.api.service.CounseleeService;
import com.springboot.enums.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApiController(name = "CounseleeController", description = "내담자 관련 API를 제공하는 Controller", path = "/v1/counsel/counselee")
@RequiredArgsConstructor
public class CounseleeController {

        private final CounseleeService counseleeService;

        @GetMapping("/{counselSessionId}/base/information")
        @Operation(summary = "내담자 기본 정보 조회", tags = { "상담 카드 작성", "상담 노트" })
        @RoleSecured({ RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER })
        public ResponseEntity<CommonRes<SelectCounseleeBaseInformationByCounseleeIdRes>> selectCounseleeBaseInformation(
                        @PathVariable("counselSessionId") @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId) {

                SelectCounseleeBaseInformationByCounseleeIdRes selectCounseleeBaseInformationByCounseleeIdRes = counseleeService
                                .selectCounseleeBaseInformation(counselSessionId);

                return ResponseEntity.ok(new CommonRes<>(selectCounseleeBaseInformationByCounseleeIdRes));
        }

        @PostMapping("/")
        @Operation(summary = "내담자 기본 정보 생성", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<String>> addCounselee(
                        @Valid @RequestBody AddCounseleeReq addCounseleeReq) {

                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.addCounselee(addCounseleeReq)));
        }

        @PutMapping("/")
        @Operation(summary = "내담자 기본 정보 수정", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<String>> updateCounselee(
                        @Valid @RequestBody UpdateCounseleeReq updateCounseleeReq) {
                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.updateCounselee(updateCounseleeReq)));
        }

        @GetMapping("/{counseleeId}")
        @Operation(summary = "내담자 상세 정보 조회", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<SelectCounseleeRes>> selectCounselee(
                        @PathVariable("counseleeId") String counseleeId) {
                return ResponseEntity
                                .ok(new CommonRes<>(counseleeService.selectCounselee(counseleeId)));
        }

        @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "내담자 목록 조회", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<SelectCounseleePageRes>> selectCounselees(
                        @RequestParam("page") @Min(0) int page,
                        @RequestParam("size") @Min(1) @Max(100) int size,
                        @RequestParam(required = false, name = "name") @Pattern(regexp = "^[가-힣a-zA-Z\\s]*$", message = "이름은 한글과 영문만 허용됩니다") String name,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") List<@Past(message = "생년월일은 과거 날짜여야 합니다") LocalDate> birthDates,
                        @RequestParam(required = false) List<@Size(max = 100, message = "기관명은 100자를 초과할 수 없습니다") String> affiliatedWelfareInstitutions) {

                if (name != null) {
                        try {
                                name = name.trim();
                                if (name.isEmpty()) {
                                        name = null;
                                }
                        } catch (Exception e) {
                                throw new IllegalArgumentException("잘못된 이름 형식입니다");
                        }
                }

                if (affiliatedWelfareInstitutions != null) {
                        affiliatedWelfareInstitutions = affiliatedWelfareInstitutions.stream()
                                        .filter(inst -> inst != null && !inst.trim().isEmpty())
                                        .map(String::trim)
                                        .collect(Collectors.toList());
                        if (affiliatedWelfareInstitutions.isEmpty()) {
                                affiliatedWelfareInstitutions = null;
                        }
                }

                return ResponseEntity
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(new CommonRes<>(counseleeService.selectCounselees(
                                                page,
                                                size,
                                                name,
                                                birthDates,
                                                affiliatedWelfareInstitutions)));
        }

        @DeleteMapping("/{counseleeId}")
        @Operation(summary = "내담자 삭제", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<String>> deleteCounselee(
                        @PathVariable("counseleeId") String counseleeId) {
                counseleeService.deleteCounselee(counseleeId);
                return ResponseEntity.ok(new CommonRes<>("delete the counselee success"));
        }

        @DeleteMapping("/batch")
        @Operation(summary = "내담자 삭제(batch)", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<List<DeleteCounseleeBatchRes>>> deleteCounseleeBatch(
                        @RequestBody @Valid List<DeleteCounseleeBatchReq> deleteCounseleeBatchReqList) {

                List<DeleteCounseleeBatchRes> deleteCounseleeBatchResList = counseleeService
                                .deleteCounseleeBatch(deleteCounseleeBatchReqList);

                return ResponseEntity.ok(new CommonRes<>(HttpMessages.SUCCESS_DELETE, deleteCounseleeBatchResList));

        }

        @GetMapping("/birth-dates")
        @Operation(summary = "등록된 생년월일 목록 조회", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<List<LocalDate>>> getBirthDates() {
                return ResponseEntity.ok(new CommonRes<>(counseleeService.getDistinctBirthDates()));
        }

        @GetMapping("/affiliated-welfare-institutions")
        @Operation(summary = "연계 기관 목록 조회", tags = { "내담자 관리" })
        @RoleSecured(RoleType.ROLE_ADMIN)
        public ResponseEntity<CommonRes<List<String>>> getAffiliatedWelfareInstitutions() {
                return ResponseEntity.ok(new CommonRes<>(counseleeService.getDistinctAffiliatedWelfareInstitutions()));
        }

}
