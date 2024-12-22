package com.springboot.api.controller;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.dto.counselcard.*;
import com.springboot.api.service.CounselCardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiController(
        path="/v1/counsel/card"
        ,name = "CounselCardController"
        , description = "상담카드 관련 API를 제공하는 Controller"
)
@RequiredArgsConstructor
public class CounselCardController {

    private final CounselCardService counselCardService;


    @GetMapping("/{counselSessionId}")
    @Operation(summary = "상담 카드 조회",tags = {"상담 카드 작성","본상담 - 상담 카드"})
    ResponseEntity<CommonRes<SelectCounselCardRes>> selectCounselCard(@PathVariable String counselSessionId) {

        SelectCounselCardRes selectCounselCardRes = counselCardService.selectCounselCard(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectCounselCardRes));
    }

    @GetMapping("/{counselSessionId}/previous")
    @Operation(summary = "이전 상담 카드 조회",tags = {"상담 카드 작성"})
    ResponseEntity<CommonRes<SelectPreviousCounselCardRes>> selectPreviousCounselCard(@PathVariable String counselSessionId) {

        SelectPreviousCounselCardRes selectPreviousCounselCardRes = counselCardService.selectPreviousCounselCard(counselSessionId);

        return ResponseEntity.ok(new CommonRes<>(selectPreviousCounselCardRes));
    }


    @PostMapping
    @Operation(summary = "상담 카드 등록",tags = {"상담 카드 작성"})
    ResponseEntity<CommonRes<AddCounselCardRes>> addCounselCard(@RequestBody @Valid AddCounselCardReq addCounselCardReq) {

        AddCounselCardRes addCounselCardRes = counselCardService.addCounselCard(addCounselCardReq);


        return ResponseEntity.ok(new CommonRes<>(addCounselCardRes));
    }

    @PutMapping
    @Operation(summary = "상담 카드 수정",tags = {"상담 카드 작성"})
    ResponseEntity<CommonRes<UpdateCounselCardRes>> updateCounselCard(@RequestBody @Valid UpdateCounselCardReq updateCounselCardReq) {

        UpdateCounselCardRes updateCounselCardRes = counselCardService.updateCounselCard(updateCounselCardReq);


        return ResponseEntity.ok(new CommonRes<>(updateCounselCardRes));
    }

    @DeleteMapping
    @Operation(summary = "상담 카드 삭제")
    ResponseEntity<CommonRes<DeleteCounselCardRes>> deleteCounselCard(@RequestBody @Valid DeleteCounselCardReq deleteCounselCardReq) {

        DeleteCounselCardRes deleteCounselCardRes = counselCardService.deleteCounselCard(deleteCounselCardReq);

        return ResponseEntity.ok(new CommonRes<>(deleteCounselCardRes));
    }

    @GetMapping("/{counselSessionId}/preious/item/list")
    @Operation(summary = "이전 상담 카드 item 목록 조회",tags = {"본상담 - 상담 카드"})
    ResponseEntity<CommonRes<List<SelectPreviousItemListByInformationNameAndItemNameRes>>> selectPreviousItemListByInformationNameAndItemName(
            @PathVariable String counselSessionId
            ,@RequestParam(required = true) String informationName
            ,@RequestParam(required = true) String itemName
    ){

        List<SelectPreviousItemListByInformationNameAndItemNameRes> selectPreviousItemListResListByInformationNameAndInformationItemName = counselCardService
                .selectPreviousItemListByInformationNameAndItemName(
                        counselSessionId
                        , informationName
                        , itemName);


        return ResponseEntity.ok(new CommonRes<>(selectPreviousItemListResListByInformationNameAndInformationItemName));
    }


}
