package com.springboot.api.tus.service;

import com.springboot.api.common.util.FileUtil;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.tus.config.TusProperties;
import com.springboot.api.tus.dto.response.TusFileInfoRes;
import com.springboot.api.tus.entity.TusFileInfo;
import com.springboot.api.tus.repository.TusFileInfoRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TusService {

    private final TusFileInfoRepository tusFileInfoRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final TusProperties tusProperties;
    private final FileUtil fileUtil;

    @Transactional
    public String initUpload(String metadata, Long contentLength, Boolean isDefer) {

        Map<String, String> parsedMetadata = parseMetadata(metadata);

        CounselSession counselSession = counselSessionRepository.findById(parsedMetadata.get("counselSessionId"))
            .orElseThrow(() -> new IllegalArgumentException("상담 세션을 찾을 수 없습니다."));

        TusFileInfo fileInfo = TusFileInfo.of(counselSession, parsedMetadata.get("filename"), contentLength, isDefer);

        tusFileInfoRepository.save(fileInfo);

        fileUtil.createUploadFile(fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension()));

        return fileInfo.getId();
    }

    private Map<String, String> parseMetadata(@NonNull String metadata) {
        return Arrays.stream(Optional.of(metadata).filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new RuntimeException("metadata 가 없습니다."))
                .split(","))
            .map(keyAndValue -> keyAndValue.split(" "))
            .collect(
                Collectors.toMap(values -> values[0], values -> new String(Base64.getDecoder().decode(values[1]))));
    }

    @Transactional(readOnly = true)
    public TusFileInfoRes getTusFileInfo(String fileId) {
        TusFileInfo fileInfo = tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("Tus 파일 정보을 찾을 수 없습니다."));

        return new TusFileInfoRes(fileInfo);
    }

    @Transactional
    public Long appendData(String fileId, long offset, ServletInputStream inputStream) {
        TusFileInfo fileInfo = tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("Tus 파일 정보을 찾을 수 없습니다."));

        if (fileInfo.getContentOffset() != offset) {
            throw new IllegalArgumentException("Offset 정보가 맞지 않습니다.");
        }

        Path path = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.APPEND)) {
            byte[] buffer = new byte[8192];
            long read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, (int) read);
                fileInfo.updateOffset(read);
            }
        } catch (IOException e) {
            throw new RuntimeException("Tus 파일 업로드에 실패했습니다.");
        }

        return fileInfo.getContentOffset();
    }

    @Transactional(readOnly = true)
    public void mergeUploadedFile(String counselSessionId) {

        List<TusFileInfo> tusFileInfoList = tusFileInfoRepository.findAllByCounselSessionIdOrderByUpdatedDatetimeAsc(
            counselSessionId);

        List<String> pathList = tusFileInfoList.stream()
            .map(tusFileInfo -> tusFileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension()))
            .map(Path::toAbsolutePath)
            .map(Path::toString)
            .toList();

        fileUtil.mergeWebmFile(pathList, tusProperties.getMergePath());
    }

    @Transactional(readOnly = true)
    public Resource getUploadedFile(String fileId) {
        TusFileInfo fileInfo = tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("Tus 파일 정보을 찾을 수 없습니다."));

        Path path = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());

        return fileUtil.getUrlResource(path);
    }
}
