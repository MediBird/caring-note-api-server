package com.springboot.api.tus.service;

import com.springboot.api.common.util.FileUtil;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.tus.config.TusProperties;
import com.springboot.api.tus.dto.response.TusFileInfoRes;
import com.springboot.api.tus.entity.SessionRecord;
import com.springboot.api.tus.entity.TusFileInfo;
import com.springboot.api.tus.repository.SessionRecordRepository;
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
import java.util.Optional;
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
    private final SessionRecordRepository sessionRecordRepository;
    private final TusProperties tusProperties;
    private final FileUtil fileUtil;

    @Transactional
    public String initUpload(String metadata) {
        String counselSessionId = extractCounselSessionId(metadata);
        CounselSession counselSession = getCounselSession(counselSessionId);
        SessionRecord sessionRecord = getOrCreateSessionRecord(counselSessionId, counselSession);
        TusFileInfo fileInfo = createAndSaveFile(sessionRecord);
        createUploadFile(fileInfo);
        return fileInfo.getLocation(tusProperties.getPathPrefix());
    }

    private String extractCounselSessionId(@NonNull String metadata) {
        return Arrays.stream(Optional.of(metadata).filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new IllegalArgumentException("metadata 가 없습니다."))
                .split(","))
            .map(kv -> {
                String[] parts = kv.split(" ", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException("메타데이터 형식이 올바르지 않습니다.");
                }
                return new String[]{parts[0], parts[1]};
            })
            .filter(pair -> "counselSessionId".equals(pair[0]))
            .findFirst()
            .map(pair -> new String(Base64.getDecoder().decode(pair[1])))
            .orElseThrow(() -> new IllegalArgumentException("counselSessionId 가 메타데이터에 없습니다."));
    }

    private CounselSession getCounselSession(String counselSessionId) {
        return counselSessionRepository.findById(counselSessionId)
            .orElseThrow(() -> new EntityNotFoundException("상담 세션을 찾을 수 없습니다. " + counselSessionId));
    }

    private SessionRecord getOrCreateSessionRecord(String counselSessionId, CounselSession counselSession) {
        return sessionRecordRepository.findByCounselSessionId(counselSessionId)
            .orElseGet(() -> sessionRecordRepository.save(SessionRecord.of(counselSession)));
    }

    private TusFileInfo createAndSaveFile(SessionRecord sessionRecord) {
        TusFileInfo fileInfo = TusFileInfo.of(sessionRecord);
        return tusFileInfoRepository.save(fileInfo);
    }

    private void createUploadFile(TusFileInfo fileInfo) {
        Path filePath = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());
        fileUtil.createUploadFile(filePath);
    }

    @Transactional(readOnly = true)
    public TusFileInfoRes getTusFileInfo(String fileId) {
        TusFileInfo fileInfo = getFileInfo(fileId);
        String location = fileInfo.getLocation(tusProperties.getPathPrefix());
        return new TusFileInfoRes(fileInfo, location);
    }

    private TusFileInfo getFileInfo(String fileId) {
        return tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new EntityNotFoundException("Tus 파일 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public Long appendData(String fileId, long offset, ServletInputStream inputStream, Long duration) {
        TusFileInfo fileInfo = getFileInfo(fileId);

        if (fileInfo.isNotOffsetEqual(offset)) {
            throw new IllegalArgumentException("Offset 정보가 맞지 않습니다.");
        }

        if (duration != null) {
            fileInfo.getSessionRecord().updateDuration(duration);
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

        List<TusFileInfo> tusFileInfoList = tusFileInfoRepository.findAllBySessionRecordCounselSessionId(
            counselSessionId);

        List<String> pathList = tusFileInfoList.stream()
            .map(tusFileInfo -> tusFileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension()))
            .map(Path::toAbsolutePath).map(Path::toString).toList();

        Path mergePath = Path.of(tusProperties.getMergePath(), counselSessionId + ".mp4");

        fileUtil.mergeWebmFile(pathList, mergePath.toAbsolutePath().toString());
    }

    @Transactional(readOnly = true)
    public Resource getUploadedFile(String fileId) {
        TusFileInfo fileInfo = getFileInfo(fileId);

        Path path = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());

        return fileUtil.getUrlResource(path);
    }

    @Transactional
    public void deleteUploadedFile(String counselSessionId) {
        SessionRecord sessionRecord = sessionRecordRepository.findByCounselSessionId(counselSessionId)
            .orElseThrow(() -> new EntityNotFoundException("Tus 녹음 정보를 찾을 수 없습니다."));

        String folderPath = sessionRecord.getFolderPath(tusProperties.getUploadPath());

        fileUtil.deleteDirectory(folderPath);
        sessionRecordRepository.delete(sessionRecord);
    }
}
