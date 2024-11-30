package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counselcard.InsertCounselCardReq;
import com.springboot.api.dto.counselcard.InsertCounselCardRes;
import com.springboot.api.service.CounselCardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiController(
        path="/v1/counsel/card"
        ,name = "CounselCardController"
        , description = "상담카드 관련 API를 제공하는 Controller"
)
public class CounselCardController {

    private final CounselCardService counselCardService;

    public CounselCardController(CounselCardService counselCardService) {
        this.counselCardService = counselCardService;
    }

    @PostMapping
    @Operation(summary = "상담 카드 등록",tags = {"상담 카드 작성"})
    ResponseEntity<CommonRes<InsertCounselCardRes>> insertCounselCard(@AuthenticationPrincipal UserDetails userDetails
                                                                    ,@RequestBody @Valid InsertCounselCardReq insertCounselCardReq) {

        InsertCounselCardRes insertCounselCardRes = counselCardService.insertCounselCard(userDetails.getUsername(), insertCounselCardReq);


        return ResponseEntity.ok(new CommonRes<>(insertCounselCardRes));
    }



}
