package com.springboot.api.tus.service;

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

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @Transactional
    public void mergeUploadedFile(String counselSessionId) {
        log.info("파일 머지 시작. counselSessionId: {}", counselSessionId);

        List<TusFileInfo> tusFileInfoList = tusFileInfoRepository.findAllByCounselSessionIdOrderByUpdatedDatetimeAsc(
            counselSessionId);
        log.info("조회된 파일 개수: {}", tusFileInfoList.size());

        if (tusFileInfoList.isEmpty()) {
            log.warn("병합할 파일이 없음. counselSessionId: {}", counselSessionId);
            throw new IllegalArgumentException("병합할 파일이 없습니다.");
        }

        List<String> pathList = tusFileInfoList.stream()
            .map(tusFileInfo -> tusFileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension()))
            .map(Path::toAbsolutePath)
            .map(Path::toString)
            .toList();
        log.info("병합 대상 파일 경로: {}", pathList);

        try {
            // 파일 유효성 사전 검증
            log.info("파일 유효성 검증 시작");
            validateFilesBeforeMerge(pathList);
            log.info("파일 유효성 검증 완료");

            Path mergePath = Path.of(tusProperties.getMergePath(), counselSessionId + ".mp4");
            log.info("머지 파일 경로: {}", mergePath.toAbsolutePath());

            log.info("FFmpeg 머지 시작");
            fileUtil.mergeWebmFile(pathList, mergePath.toAbsolutePath().toString());
            log.info("FFmpeg 머지 완료. counselSessionId: {}", counselSessionId);
            
        } catch (IllegalArgumentException e) {
            log.error("파일 유효성 검증 실패. counselSessionId: {}", counselSessionId, e);
            cleanupFailedMerge(counselSessionId);
            throw new RuntimeException("파일 검증 실패로 인해 업로드된 파일들을 모두 삭제했습니다. 다시 업로드해 주세요.", e);
        } catch (RuntimeException e) {
            // 머지 실패 시 업로드된 파일들과 DB 레코드 모두 삭제
            log.error("FFmpeg 머지 실패로 인한 파일 정리 시작. counselSessionId: {}", counselSessionId, e);
            cleanupFailedMerge(counselSessionId);
            throw new RuntimeException("머지 실패로 인해 업로드된 파일들을 모두 삭제했습니다. 다시 업로드해 주세요.", e);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생. counselSessionId: {}", counselSessionId, e);
            cleanupFailedMerge(counselSessionId);
            throw new RuntimeException("파일 머지 중 예상치 못한 오류가 발생했습니다. 업로드된 파일들을 정리했습니다.", e);
        }
    }
    
    private void validateFilesBeforeMerge(List<String> pathList) {
        log.info("파일 유효성 검증 대상 개수: {}", pathList.size());
        
        for (int i = 0; i < pathList.size(); i++) {
            String pathStr = pathList.get(i);
            log.debug("파일 검증 중 ({}/{}): {}", i + 1, pathList.size(), pathStr);
            
            Path path = Path.of(pathStr);
            if (!Files.exists(path)) {
                log.error("파일이 존재하지 않음: {}", pathStr);
                throw new IllegalArgumentException("병합 대상 파일이 존재하지 않습니다: " + pathStr);
            }
            
            try {
                long fileSize = Files.size(path);
                log.debug("파일 크기: {} bytes ({})", fileSize, pathStr);
                
                if (fileSize == 0) {
                    log.error("빈 파일 발견: {}", pathStr);
                    throw new IllegalArgumentException("병합 대상 파일이 비어있습니다: " + pathStr);
                }
                
                // 최소 webm 파일 크기 검증 (매우 작은 파일은 손상되었을 가능성이 높음)
                if (fileSize < 100) { // 100바이트 미만은 유효한 webm 파일이 아닐 가능성이 높음
                    log.warn("의심스럽게 작은 webm 파일 발견: {} (크기: {} bytes)", pathStr, fileSize);
                    throw new IllegalArgumentException("파일이 너무 작아서 유효하지 않을 수 있습니다: " + pathStr);
                }
                
            } catch (IOException e) {
                log.error("파일 정보 읽기 실패: {}", pathStr, e);
                throw new IllegalArgumentException("파일 정보를 읽을 수 없습니다: " + pathStr, e);
            }
        }
        
        log.info("모든 파일 유효성 검증 완료");
    }
    
    @Transactional
    public void cleanupFailedMerge(String counselSessionId) {
        try {
            // 1. 업로드 파일 디렉토리 삭제
            String folderPath = Path.of(tusProperties.getUploadPath(), counselSessionId).toAbsolutePath().toString();
            fileUtil.deleteDirectory(folderPath);
            
            // 2. 머지 파일이 생성되었다면 삭제
            Path mergePath = Path.of(tusProperties.getMergePath(), counselSessionId + ".mp4");
            if (Files.exists(mergePath)) {
                Files.delete(mergePath);
            }
            
            // 3. 데이터베이스 레코드 삭제
            tusFileInfoRepository.deleteAllByCounselSessionId(counselSessionId);
            
            log.info("머지 실패로 인한 파일 정리 완료. counselSessionId: {}", counselSessionId);
        } catch (Exception cleanupException) {
            log.error("파일 정리 중 오류 발생. counselSessionId: {}", counselSessionId, cleanupException);
        }
    }

    @Transactional(readOnly = true)
    public Resource getUploadedFile(String fileId) {
        TusFileInfo fileInfo = tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("Tus 파일 정보를 찾을 수 없습니다."));

        Path path = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());

        return fileUtil.getUrlResource(path);
    }

    @Transactional
    public void deleteUploadedFile(String fileId) {
        TusFileInfo fileInfo = tusFileInfoRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("Tus 파일 정보를 찾을 수 없습니다."));

        // 개별 파일 삭제
        Path filePath = fileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
        }

        // DB 레코드 삭제
        tusFileInfoRepository.delete(fileInfo);
    }

    @Transactional
    public void deleteUploadedFilesByCounselSession(String counselSessionId) {
        String folderPath = Path.of(tusProperties.getUploadPath(), counselSessionId).toAbsolutePath().toString();

        fileUtil.deleteDirectory(folderPath);

        tusFileInfoRepository.deleteAllByCounselSessionId(counselSessionId);
    }

    @Transactional(readOnly = true)
    public boolean validateUploadedFiles(String counselSessionId) {
        List<TusFileInfo> tusFileInfoList = tusFileInfoRepository.findAllByCounselSessionIdOrderByUpdatedDatetimeAsc(
            counselSessionId);

        if (tusFileInfoList.isEmpty()) {
            return false;
        }

        List<String> pathList = tusFileInfoList.stream()
            .map(tusFileInfo -> tusFileInfo.getFilePath(tusProperties.getUploadPath(), tusProperties.getExtension()))
            .map(Path::toAbsolutePath)
            .map(Path::toString)
            .toList();

        try {
            validateFilesBeforeMerge(pathList);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("파일 검증 실패. counselSessionId: {}, 오류: {}", counselSessionId, e.getMessage());
            throw e;
        }
    }
}
