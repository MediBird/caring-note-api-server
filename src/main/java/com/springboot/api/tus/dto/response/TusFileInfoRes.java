package com.springboot.api.tus.dto.response;

import static com.springboot.api.tus.config.TusConstant.URL_PREFIX;

import com.springboot.api.tus.entity.TusFileInfo;
import lombok.Getter;

@Getter
public class TusFileInfoRes {

    private final String location;
    private final Long contentLength;
    private final Long contentOffset;
    private final Boolean isDefer;

    public TusFileInfoRes(TusFileInfo tusFileInfo) {
        this.location = URL_PREFIX + "/" + tusFileInfo.getCounselSession().getId() + "/" + tusFileInfo.getId();
        this.contentLength = tusFileInfo.getContentLength();
        this.contentOffset = tusFileInfo.getContentOffset();
        this.isDefer = tusFileInfo.getIsDefer();
    }
}
