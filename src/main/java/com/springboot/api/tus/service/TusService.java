package com.springboot.api.tus.service;

import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.tus.config.TusProperties;
import com.springboot.api.tus.dto.response.TusFileInfoRes;
import com.springboot.api.tus.entity.TusFileInfo;
import com.springboot.api.tus.repository.TusFileInfoRepository;
import com.springboot.api.tus.util.TusUtil;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TusService {

    private final TusFileInfoRepository tusFileInfoRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final TusProperties tusProperties;

    @Transactional
    public String initUpload(String metadata, Long contentLength, Boolean isDefer) {

        Map<String, String> parsedMetadata = TusUtil.parseMetadata(metadata);

        CounselSession counselSession = counselSessionRepository.findById(parsedMetadata.get("counselSessionId"))
            .orElseThrow(() -> new IllegalArgumentException("상담 세션을 찾을 수 없습니다."));

        TusFileInfo fileInfo = TusFileInfo.of(counselSession, parsedMetadata.get("filename"), contentLength, isDefer);

        tusFileInfoRepository.save(fileInfo);

        createUploadFile(fileInfo);

        return fileInfo.getId();
    }

    private void createUploadFile(TusFileInfo fileInfo) {
        try {
            Path path = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException("Tus 업로드 파일 생성에 실패했습니다.");
        }
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
}
