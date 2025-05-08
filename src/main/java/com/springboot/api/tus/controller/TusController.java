package com.springboot.api.tus.controller;

import static com.springboot.api.tus.config.TusConstant.ACCESS_CONTROL_ALLOW_METHODS_HEADER;
import static com.springboot.api.tus.config.TusConstant.ACCESS_CONTROL_ALLOW_METHODS_VALUE;
import static com.springboot.api.tus.config.TusConstant.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER;
import static com.springboot.api.tus.config.TusConstant.ACCESS_CONTROL_ALLOW_ORIGIN_VALUE;
import static com.springboot.api.tus.config.TusConstant.ACCESS_CONTROL_EXPOSE_HEADER;
import static com.springboot.api.tus.config.TusConstant.ACCESS_CONTROL_EXPOSE_OPTIONS_VALUE;
import static com.springboot.api.tus.config.TusConstant.ACCESS_CONTROL_EXPOSE_POST_VALUE;
import static com.springboot.api.tus.config.TusConstant.AUDIO_WEBM;
import static com.springboot.api.tus.config.TusConstant.CACHE_CONTROL_HEADER;
import static com.springboot.api.tus.config.TusConstant.CACHE_CONTROL_VALUE;
import static com.springboot.api.tus.config.TusConstant.LOCATION_HEADER;
import static com.springboot.api.tus.config.TusConstant.OFFSET_OCTET_STREAM;
import static com.springboot.api.tus.config.TusConstant.TUS_EXTENSION_HEADER;
import static com.springboot.api.tus.config.TusConstant.TUS_RESUMABLE_HEADER;
import static com.springboot.api.tus.config.TusConstant.TUS_RESUMABLE_VALUE;
import static com.springboot.api.tus.config.TusConstant.TUS_VERSION_HEADER;
import static com.springboot.api.tus.config.TusConstant.TUS_VERSION_VALUE;
import static com.springboot.api.tus.config.TusConstant.UPLOAD_DEFER_LENGTH_HEADER;
import static com.springboot.api.tus.config.TusConstant.UPLOAD_LENGTH_HEADER;
import static com.springboot.api.tus.config.TusConstant.UPLOAD_METADATA;
import static com.springboot.api.tus.config.TusConstant.UPLOAD_OFFSET_HEADER;
import static com.springboot.api.tus.config.TusConstant.URL_PREFIX;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.tus.dto.response.TusFileInfoRes;
import com.springboot.api.tus.service.TusService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiController(name = "TusController", description = "Tus 프로토콜 구현 Controller", path = URL_PREFIX)
@RequiredArgsConstructor
public class TusController {

    private final TusService tusService;

    @Operation(summary = "서버의 tus 업로드 지원 버전 및 확장 정보를 반환합니다.", tags = {"TUS"})
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Object> processOptions() {
        return ResponseEntity.noContent()
            .header(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, ACCESS_CONTROL_ALLOW_ORIGIN_VALUE)
            .header(ACCESS_CONTROL_EXPOSE_HEADER, ACCESS_CONTROL_EXPOSE_OPTIONS_VALUE)
            .header(TUS_RESUMABLE_HEADER, TUS_RESUMABLE_VALUE)
            .header(TUS_VERSION_HEADER, TUS_VERSION_VALUE)
            .header(TUS_EXTENSION_HEADER, TUS_EXTENSION_HEADER)
            .header(ACCESS_CONTROL_ALLOW_METHODS_HEADER, ACCESS_CONTROL_ALLOW_METHODS_VALUE)
            .build();
    }

    @Operation(summary = "새로운 tus 업로드 리소스를 생성합니다.", tags = {"TUS"})
    @PostMapping
    public ResponseEntity<Object> startUpload(
        @NotNull @RequestHeader(name = UPLOAD_METADATA) final String metadata,
        @RequestHeader(name = UPLOAD_LENGTH_HEADER, required = false) final Long contentLength,
        @RequestHeader(name = UPLOAD_DEFER_LENGTH_HEADER, required = false) final Boolean isDefer
    ) {
        String fileId = tusService.initUpload(metadata, contentLength, isDefer);

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(ACCESS_CONTROL_EXPOSE_HEADER, ACCESS_CONTROL_EXPOSE_POST_VALUE)
            .header(LOCATION_HEADER, URL_PREFIX + "/" + fileId)
            .header(TUS_RESUMABLE_HEADER, TUS_RESUMABLE_VALUE)
            .build();
    }

    @Operation(summary = "지정된 업로드 리소스의 현재 업로드 오프셋과 길이를 조회합니다.", tags = {"TUS"})
    @RequestMapping(method = RequestMethod.HEAD, value = "/{fileId}")
    public ResponseEntity<Object> getUploadStatus(@PathVariable final String fileId) {
        TusFileInfoRes tusFileInfo = tusService.getTusFileInfo(fileId);

        if (Boolean.TRUE.equals(tusFileInfo.getIsDefer())) {
            return ResponseEntity.noContent()
                .header(LOCATION_HEADER, tusFileInfo.getLocation())
                .header(CACHE_CONTROL_HEADER, CACHE_CONTROL_VALUE)
                .header(UPLOAD_DEFER_LENGTH_HEADER, "1")
                .header(UPLOAD_OFFSET_HEADER, String.valueOf(tusFileInfo.getContentOffset()))
                .header(TUS_RESUMABLE_HEADER, TUS_RESUMABLE_VALUE)
                .build();
        }
        return ResponseEntity.noContent()
            .header(LOCATION_HEADER, tusFileInfo.getLocation())
            .header(CACHE_CONTROL_HEADER, CACHE_CONTROL_VALUE)
            .header(UPLOAD_LENGTH_HEADER, String.valueOf(tusFileInfo.getContentLength()))
            .header(UPLOAD_OFFSET_HEADER, String.valueOf(tusFileInfo.getContentOffset()))
            .header(TUS_RESUMABLE_HEADER, TUS_RESUMABLE_VALUE)
            .build();
    }

    @Operation(summary = "업로드 리소스에 데이터를 이어서 전송하고 오프셋을 갱신합니다.", tags = {"TUS"})
    @PatchMapping(value = "/{fileId}", consumes = {OFFSET_OCTET_STREAM})
    public ResponseEntity<Object> uploadProcess(
        @NonNull @PathVariable("fileId") final String fileId,
        @NonNull final HttpServletRequest request,
        @RequestHeader(name = UPLOAD_OFFSET_HEADER) final long offset
    ) {
        try {
            Long nextOffset = tusService.appendData(fileId, offset, request.getInputStream());

            return ResponseEntity.noContent()
                .header(UPLOAD_OFFSET_HEADER, String.valueOf(nextOffset))
                .header(TUS_RESUMABLE_HEADER, TUS_RESUMABLE_VALUE)
                .build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "업로드한 상담세션 녹음 파일을 병합합니다.", tags = {"TUS"})
    @GetMapping(value = "/merge/{counselSessionId}")
    public ResponseEntity<Object> mergeMediaFile(
        @PathVariable("counselSessionId") final String counselSessionId
    ) {

        tusService.mergeUploadedFile(counselSessionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "업로드한 상담세션 녹음 파일을 다운로드 합니다.", tags = {"TUS"})
    @GetMapping(value = "/{fileId}")
    public ResponseEntity<Resource> getMediaFile(@PathVariable("fileId") final String fileId) {
        Resource uploadedFile = tusService.getUploadedFile(fileId);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(AUDIO_WEBM))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + uploadedFile.getFilename() + "\"")
            .body(uploadedFile);
    }
}
