package com.springboot.api.tus.dto.response;

import com.springboot.api.tus.entity.TusFileInfo;

import lombok.Getter;

@Getter
public class TusFileInfoRes {

    private final String fileId;
    private final String originalFilename;
    private final Long contentLength;
    private final Long contentOffset;
    private final Boolean isDefer;
    private final String location;
    private final Long duration;

    public TusFileInfoRes(TusFileInfo tusFileInfo, String location) {
        this.fileId = tusFileInfo.getId();
        this.originalFilename = tusFileInfo.getOriginalName();
        this.contentLength = tusFileInfo.getContentLength();
        this.contentOffset = tusFileInfo.getContentOffset();
        this.isDefer = tusFileInfo.getIsDefer();
        this.location = location;
        this.duration = tusFileInfo.getDuration();
    }

    // 상담세션 전체 정보를 위한 생성자
    public TusFileInfoRes(String counselSessionId, Long contentLength, Long contentOffset, Boolean isDefer, String location, Long duration) {
        this.fileId = counselSessionId;
        this.originalFilename = null; // 세션 전체 정보에서는 개별 파일명이 의미 없음
        this.contentLength = contentLength;
        this.contentOffset = contentOffset;
        this.isDefer = isDefer;
        this.location = location;
        this.duration = duration;
    }
}
