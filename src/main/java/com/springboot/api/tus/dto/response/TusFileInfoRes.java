package com.springboot.api.tus.dto.response;

import com.springboot.api.tus.entity.TusFileInfo;
import lombok.Getter;

@Getter
public class TusFileInfoRes {

    private final String fileId;
    private final Long contentOffset;
    private final String location;
    private final Long duration;

    public TusFileInfoRes(TusFileInfo tusFileInfo, String location) {
        this.fileId = tusFileInfo.getId();
        this.contentOffset = tusFileInfo.getContentOffset();
        this.location = location;
        this.duration = tusFileInfo.getSessionRecord().getDuration();
    }
}
