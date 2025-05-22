package com.springboot.api.tus.service;

import com.springboot.api.common.util.FileUtil;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.tus.config.TusProperties;
import com.springboot.api.tus.dto.response.TusFileInfoRes;
import com.springboot.api.tus.entity.TusFileInfo;
import com.springboot.api.tus.repository.TusFileInfoRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TusService {

    private final TusFileInfoRepository tusFileInfoRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final TusProperties tusProperties;
    private final FileUtil fileUtil;

    @Transactional
    public String initUpload(String metadata, Long contentLength, Boolean isDefer) {

        Map<String, String> parsedMetadata = parseMetadata(metadata);

        CounselSession counselSession = counselSessionRepository.findById(parsedMetadata.get("counselSessionId"))
            .orElseThrow(() -> new EntityNotFoundException("상담 세션을 찾을 수 없습니다."));

        TusFileInfo fileInfo = TusFileInfo.of(counselSession, parsedMetadata.get("filename"), contentLength, isDefer);

        tusFileInfoRepository.save(fileInfo);

        fileUtil.createUploadFile(fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension()));

        return fileInfo.getId();
    }

    private Map<String, String> parseMetadata(@NonNull String metadata) {
        return Arrays.stream(Optional.of(metadata).filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new RuntimeException("metadata 가 없습니다."))
                .split(","))
            .map(keyAndValue -> {
                String[] values = keyAndValue.split(" ", 2);
                if (values.length != 2) {
                    throw new IllegalArgumentException("메타데이터 형식이 올바르지 않습니다.");
                }
                return new String[]{values[0], values[1]};
            })
            .collect(
                Collectors.toMap(values -> values[0], values -> new String(Base64.getDecoder().decode(values[1]))));
    }

    @Transactional(readOnly = true)
    public TusFileInfoRes getTusFileInfo(String fileId) {
        TusFileInfo fileInfo = tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("Tus 파일 정보를 찾을 수 없습니다."));

        String location =
            tusProperties.getPathPrefix() + "/" + fileInfo.getCounselSession().getId() + "/" + fileInfo.getId();

        return new TusFileInfoRes(fileInfo, location);
    }

    @Transactional
    public Long appendData(String fileId, long offset, ServletInputStream inputStream, Long duration) {
        TusFileInfo fileInfo = tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("Tus 파일 정보를 찾을 수 없습니다."));

        if (fileInfo.getContentOffset() != offset) {
            throw new IllegalArgumentException("Offset 정보가 맞지 않습니다.");
        }

        if (duration != null) {
            fileInfo.updateDuration(duration);
        }

        Path path = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.APPEND)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                fileInfo.updateOffset(bytesRead);
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

        Path mergePath = Path.of(tusProperties.getMergePath(), counselSessionId + ".mp4");

        fileUtil.mergeAndConvertWebmFile(pathList, mergePath.toAbsolutePath().toString());
    }

    @Transactional(readOnly = true)
    public Resource getUploadedFile(String fileId) {
        TusFileInfo fileInfo = tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("Tus 파일 정보를 찾을 수 없습니다."));

        Path path = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());

        return fileUtil.getUrlResource(path);
    }

    @Transactional
    public void deleteUploadedFile(String counselSessionId) {
        String folderPath = Path.of(tusProperties.getUploadPath(), counselSessionId).toAbsolutePath().toString();

        fileUtil.deleteDirectory(folderPath);

        tusFileInfoRepository.deleteAllByCounselSessionId(counselSessionId);
    }
}
