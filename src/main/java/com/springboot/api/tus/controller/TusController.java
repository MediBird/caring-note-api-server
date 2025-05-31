package com.springboot.api.tus.controller;

import static com.springboot.api.tus.config.TusHeaderKeys.ACCESS_CONTROL_ALLOW_METHODS_VALUE;
import static com.springboot.api.tus.config.TusHeaderKeys.ACCESS_CONTROL_ALLOW_ORIGIN_VALUE;
import static com.springboot.api.tus.config.TusHeaderKeys.ACCESS_CONTROL_EXPOSE_OPTIONS_VALUE;
import static com.springboot.api.tus.config.TusHeaderKeys.ACCESS_CONTROL_EXPOSE_POST_VALUE;
import static com.springboot.api.tus.config.TusHeaderKeys.API_URL_PREFIX;
import static com.springboot.api.tus.config.TusHeaderKeys.CACHE_CONTROL_VALUE;
import static com.springboot.api.tus.config.TusHeaderKeys.CONTENT_TYPE_AUDIO_WEBM;
import static com.springboot.api.tus.config.TusHeaderKeys.CONTENT_TYPE_OFFSET_OCTET_STREAM;
import static com.springboot.api.tus.config.TusHeaderKeys.TUS_EXTENSION_VALUE;
import static com.springboot.api.tus.config.TusHeaderKeys.TUS_RESUMABLE_VALUE;
import static com.springboot.api.tus.config.TusHeaderKeys.TUS_VERSION_VALUE;

import com.springboot.api.common.annotation.ApiController;
import com.springboot.api.tus.config.TusHeaderKeys;
import com.springboot.api.tus.config.TusProperties;
import com.springboot.api.tus.dto.response.TusFileInfoRes;
import com.springboot.api.tus.service.TusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiController(name = "TusController", description = "Tus 프로토콜 구현 Controller", path = API_URL_PREFIX)
@RequiredArgsConstructor
public class TusController {

    private final TusService tusService;
    private final TusProperties tusProperties;

    @Operation(summary = "서버의 tus 업로드 지원 버전 및 확장 정보를 반환합니다.", tags = {"TUS"})
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Object> processOptions() {
        return ResponseEntity.noContent()
            .header(TusHeaderKeys.ACCESS_CONTROL_ALLOW_ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN_VALUE)
            .header(TusHeaderKeys.ACCESS_CONTROL_EXPOSE_HEADERS, ACCESS_CONTROL_EXPOSE_OPTIONS_VALUE)
            .header(TusHeaderKeys.TUS_RESUMABLE, TUS_RESUMABLE_VALUE)
            .header(TusHeaderKeys.TUS_VERSION, TUS_VERSION_VALUE)
            .header(TusHeaderKeys.TUS_EXTENSION, TUS_EXTENSION_VALUE)
            .header(TusHeaderKeys.ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_VALUE)
            .build();
    }

    @Operation(summary = "새로운 tus 업로드 리소스를 생성합니다. X-Recording-Duration 헤더로 녹음 길이(초)를 전달할 수 있습니다.", tags = {"TUS"})
    @Parameter(name = TusHeaderKeys.UPLOAD_METADATA, description = "업로드 메타데이터", required = true, in = ParameterIn.HEADER)
    @Parameter(name = TusHeaderKeys.UPLOAD_DEFER_LENGTH, description = "업로드 크기 지연 여부 (1이면 true)", required = true, in = ParameterIn.HEADER)
    @PostMapping
    public ResponseEntity<Object> startUpload(
        @NotNull @RequestHeader(name = TusHeaderKeys.UPLOAD_METADATA) final String metadata,
        @RequestHeader(name = TusHeaderKeys.UPLOAD_DEFER_LENGTH) final Boolean isDefer
    ) {
        String fileId = tusService.initUpload(metadata);

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(TusHeaderKeys.ACCESS_CONTROL_EXPOSE_HEADERS, ACCESS_CONTROL_EXPOSE_POST_VALUE)
            .header(TusHeaderKeys.LOCATION, tusProperties.getPathPrefix() + "/" + fileId)
            .header(TusHeaderKeys.TUS_RESUMABLE, TUS_RESUMABLE_VALUE)
            .build();
    }

    @Operation(summary = "지정된 업로드 리소스의 현재 업로드 오프셋, 길이 및 녹음 길이를 조회합니다.", tags = {"TUS"})
    @RequestMapping(method = RequestMethod.HEAD, value = "/{tusFileId}")
    public ResponseEntity<Object> getUploadStatus(@PathVariable final String tusFileId) {
        TusFileInfoRes tusFileInfo = tusService.getTusFileInfo(tusFileId);

        return ResponseEntity.noContent()
            .header(TusHeaderKeys.LOCATION, tusFileInfo.getLocation())
            .header(TusHeaderKeys.CACHE_CONTROL, CACHE_CONTROL_VALUE)
            .header(TusHeaderKeys.UPLOAD_DEFER_LENGTH, "1")
            .header(TusHeaderKeys.UPLOAD_OFFSET, String.valueOf(tusFileInfo.getContentOffset()))
            .header(TusHeaderKeys.TUS_RESUMABLE, TUS_RESUMABLE_VALUE)
            .header(TusHeaderKeys.X_RECORDING_DURATION, String.valueOf(tusFileInfo.getDuration()))
            .build();
    }

    @Operation(summary = "업로드 리소스에 데이터를 이어서 전송하고 오프셋을 갱신합니다. X-Recording-Duration 헤더로 현재까지의 녹음 길이(초)를 전달할 수 있습니다.", tags = {
        "TUS"})
    @Parameter(name = TusHeaderKeys.UPLOAD_OFFSET, description = "현재 파일 오프셋", required = true, in = ParameterIn.HEADER)
    @Parameter(name = TusHeaderKeys.X_RECORDING_DURATION, description = "현재까지의 녹음 길이 (초 단위)", required = false, in = ParameterIn.HEADER)
    @RequestBody(content = @Content(mediaType = CONTENT_TYPE_OFFSET_OCTET_STREAM, schema = @Schema(type = "string", format = "binary")))
    @PatchMapping(value = "/{fileId}", consumes = {CONTENT_TYPE_OFFSET_OCTET_STREAM})
    public ResponseEntity<Object> uploadProcess(
        @NonNull @PathVariable("fileId") final String fileId,
        @NonNull final HttpServletRequest request,
        @RequestHeader(name = TusHeaderKeys.UPLOAD_OFFSET) final long offset,
        @RequestHeader(name = TusHeaderKeys.X_RECORDING_DURATION, required = false) final Long duration
    ) {
        try {
            Long nextOffset = tusService.appendData(fileId, offset, request.getInputStream(), duration);

            return ResponseEntity.noContent()
                .header(TusHeaderKeys.UPLOAD_OFFSET, String.valueOf(nextOffset))
                .header(TusHeaderKeys.TUS_RESUMABLE, TUS_RESUMABLE_VALUE)
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
            .contentType(MediaType.parseMediaType(CONTENT_TYPE_AUDIO_WEBM))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + uploadedFile.getFilename() + "\"")
            .body(uploadedFile);
    }

    @Operation(summary = "업로드된 tus 파일을 삭제합니다.", tags = {"TUS"})
    @DeleteMapping(value = "/{fileId}")
    public ResponseEntity<Object> deleteUploadedFile(@PathVariable("fileId") final String fileId) {
        tusService.deleteUploadedFile(fileId);
        return ResponseEntity.noContent()
            .header(TusHeaderKeys.TUS_RESUMABLE, TUS_RESUMABLE_VALUE)
            .build();
    }
}
