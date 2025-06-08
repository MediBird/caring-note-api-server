package com.springboot.api.counselor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import com.springboot.api.counselor.dto.ChangePasswordReq;
import com.springboot.api.counselor.dto.CounselorInfoListRes;
import com.springboot.api.counselor.dto.CounselorNameListRes;
import com.springboot.api.counselor.dto.GetCounselorRes;
import com.springboot.api.counselor.dto.ResetPasswordReq;
import com.springboot.api.counselor.dto.UpdateCounselorReq;
import com.springboot.api.counselor.dto.UpdateCounselorRes;
import com.springboot.api.counselor.dto.UpdateMyInfoReq;
import com.springboot.api.counselor.dto.UpdateRoleReq;
import com.springboot.api.counselor.service.CounselorService;
import com.springboot.enums.RoleType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@ApiController(name = "CounselorController", description = "유저 관리 API를 제공하는 Controller", path = "/v1/counselor")
@RequiredArgsConstructor
public class CounselorController {

    private final CounselorService counselorService;

    @Operation(summary = "내 정보 조회", description = "내 정보를 조회한다.", responses = {
        @ApiResponse(responseCode = "200", description = "내 정보 조회 성공")
    })
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER, RoleType.ROLE_NONE})
    @GetMapping("/my-info")
    public ResponseEntity<GetCounselorRes> getMyInfo() {
        return ResponseEntity.ok(counselorService.getMyInfo());
    }

    @Operation(summary = "상담사 이름 목록 조회", description = "모든 상담사의 이름 목록을 조회한다. 결과는 캐싱됩니다.", responses = {
        @ApiResponse(responseCode = "200", description = "상담사 이름 목록 조회 성공")
    })
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER})
    @GetMapping("/names")
    public ResponseEntity<CounselorNameListRes> getCounselorNames() {
        return ResponseEntity.ok(counselorService.getCounselorNames());
    }

    @Operation(summary = "상담사 정보 업데이트", description = "상담사 정보를 업데이트한다.", responses = {
        @ApiResponse(responseCode = "200", description = "상담사 정보 업데이트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "상담사를 찾을 수 없음")
    })
    @RoleSecured({RoleType.ROLE_ADMIN})
    @PutMapping("/{counselorId}")
    public ResponseEntity<UpdateCounselorRes> updateCounselor(
        @PathVariable String counselorId,
        @RequestBody UpdateCounselorReq updateCounselorReq) {
        return ResponseEntity.ok(counselorService.updateCounselor(counselorId, updateCounselorReq));
    }

    @Operation(summary = "상담사 삭제", description = "상담사를 삭제한다. Keycloak에서도 해당 사용자를 삭제한다.", responses = {
        @ApiResponse(responseCode = "204", description = "상담사 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "상담사를 찾을 수 없음")
    })
    @RoleSecured({RoleType.ROLE_ADMIN})
    @DeleteMapping("/{counselorId}")
    public ResponseEntity<Void> deleteCounselor(@PathVariable String counselorId) {
        counselorService.deleteCounselor(counselorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상담사 비밀번호 초기화", description = "상담사의 비밀번호를 초기화한다. Keycloak에서 해당 사용자의 비밀번호를 초기화한다.", responses = {
        @ApiResponse(responseCode = "204", description = "비밀번호 초기화 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "상담사를 찾을 수 없음")
    })
    @RoleSecured({RoleType.ROLE_ADMIN})
    @PostMapping("/{counselorId}/reset-password")
    public ResponseEntity<Void> resetPassword(
        @PathVariable String counselorId,
        @Valid @RequestBody ResetPasswordReq resetPasswordReq) {
        counselorService.resetPassword(counselorId, resetPasswordReq);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상담사 권한 변경", description = "상담사의 권한을 변경한다. DB와 Keycloak 모두에서 권한을 업데이트한다.", responses = {
        @ApiResponse(responseCode = "200", description = "권한 변경 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "상담사를 찾을 수 없음")
    })
    @RoleSecured({RoleType.ROLE_ADMIN})
    @PutMapping("/{counselorId}/role")
    public ResponseEntity<UpdateCounselorRes> updateRole(
        @PathVariable String counselorId,
        @Valid @RequestBody UpdateRoleReq updateRoleReq) {
        return ResponseEntity.ok(counselorService.updateRole(counselorId, updateRoleReq));
    }

    @Operation(summary = "상담사 목록 페이지네이션 조회", description = "상담사 목록을 페이지네이션 형태로 조회한다.", responses = {
        @ApiResponse(responseCode = "200", description = "상담사 목록 조회 성공")
    })
    @RoleSecured({RoleType.ROLE_ADMIN})
    @GetMapping("/page")
    public ResponseEntity<PageRes<CounselorInfoListRes>> getCounselorsByPage(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(counselorService.getCounselorsByPage(PageReq.of(page, size)));
    }

    @Operation(summary = "내 정보 업데이트", description = "자기 자신의 이름과 전화번호를 업데이트한다.", responses = {
        @ApiResponse(responseCode = "200", description = "내 정보 업데이트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER, RoleType.ROLE_NONE})
    @PutMapping("/my-info")
    public ResponseEntity<UpdateCounselorRes> updateMyInfo(
        @Valid @RequestBody UpdateMyInfoReq updateMyInfoReq) {
        return ResponseEntity.ok(counselorService.updateMyInfo(updateMyInfoReq));
    }

    @Operation(summary = "내 비밀번호 변경", description = "자기 자신의 비밀번호를 변경한다. Keycloak에서 비밀번호를 변경한다.", responses = {
        @ApiResponse(responseCode = "204", description = "비밀번호 변경 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER, RoleType.ROLE_NONE})
    @PutMapping("/my-password")
    public ResponseEntity<Void> changeMyPassword(
        @Valid @RequestBody ChangePasswordReq changePasswordReq) {
        counselorService.changeMyPassword(changePasswordReq);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 계정 탈퇴", description = "자기 자신의 계정을 탈퇴한다. Keycloak에서도 해당 사용자를 삭제한다.", responses = {
        @ApiResponse(responseCode = "204", description = "계정 탈퇴 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @RoleSecured({RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN, RoleType.ROLE_USER, RoleType.ROLE_NONE})
    @DeleteMapping("/my-account")
    public ResponseEntity<Void> deleteMyAccount() {
        counselorService.deleteMyAccount();
        return ResponseEntity.noContent().build();
    }
}
