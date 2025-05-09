package com.springboot.api.tus.dto.response;

import com.springboot.api.tus.entity.TusFileInfo;
import lombok.Getter;

@Getter
public class TusFileInfoRes {

    private final String location;
    private final Long contentLength;
    private final Long contentOffset;
    private final Boolean isDefer;

    public TusFileInfoRes(TusFileInfo tusFileInfo, String location) {
        this.location = location;
        this.contentLength = tusFileInfo.getContentLength();
        this.contentOffset = tusFileInfo.getContentOffset();
        this.isDefer = tusFileInfo.getIsDefer();
    }
}
