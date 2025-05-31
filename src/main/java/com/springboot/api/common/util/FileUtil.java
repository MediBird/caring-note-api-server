package com.springboot.api.common.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.api.common.properties.FfmpegProperties;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

@Component
@RequiredArgsConstructor
public class FileUtil {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FileUtil.class);

    private final FfmpegProperties ffmpegProperties;

    public String saveMultipartFile(@NotNull MultipartFile multipartFile, @NotNull String saveFilePath)
        throws IOException {

        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Path dirPath = Path.of(saveFilePath);

        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String hash = new ULID().nextULID();
        String hashedFilename = hash + multipartFile.getOriginalFilename();
        hashedFilename = hashedFilename.replaceAll("\\s+", "");

        log.debug(hashedFilename);

        Path filePath = dirPath.resolve(hashedFilename);

        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return hashedFilename;

    }

    public File convertWebmToMp4(String fileName, String originFilePath, String convertFilePath)
        throws IOException {

        String convertedMultipartFileName = fileName.replace(".webm", ".mp4");

        FFmpeg ffmpeg = new FFmpeg(ffmpegProperties.getPath());

        FFmpegBuilder builder = new FFmpegBuilder()
            .setInput(originFilePath + fileName)
            .overrideOutputFiles(true)
            .addOutput(convertFilePath + convertedMultipartFileName) // 출력 파일 설정
            .setFormat("mp4") // 출력 포맷 설정
            .setAudioCodec("aac") // 오디오 코덱 설정 (AAC)
            .setAudioBitRate(192000) // 오디오 비트레이트 설정 (192kbps)
            .done();

        ffmpeg.run(builder);

        return Path.of(convertFilePath + convertedMultipartFileName).toFile();
    }


    public void createUploadFile(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException("업로드 파일 생성에 실패했습니다.");
        }
    }

    public void mergeWebmFile(List<String> fileList, String outputFilePath) {
        if (fileList == null || fileList.isEmpty()) {
            throw new IllegalArgumentException("병합할 파일 목록이 비어있습니다.");
        }

        // 파일 존재 여부 및 유효성 검사
        for (String filePath : fileList) {
            Path file = Path.of(filePath);
            if (!Files.exists(file)) {
                log.error("병합 대상 파일이 존재하지 않습니다: {}", filePath);
                throw new RuntimeException("병합 대상 파일이 존재하지 않습니다: " + filePath);
            }
            
            try {
                if (Files.size(file) == 0) {
                    log.error("병합 대상 파일이 비어있습니다: {}", filePath);
                    throw new RuntimeException("병합 대상 파일이 비어있습니다: " + filePath);
                }
            } catch (IOException e) {
                log.error("병합 대상 파일 크기 확인 실패: {}", filePath, e);
                throw new RuntimeException("병합 대상 파일 크기 확인 실패: " + filePath, e);
            }
        }

        log.info("FFmpeg 머지 시작. 파일 개수: {}, 출력: {}", fileList.size(), outputFilePath);
        
        try {
            // 출력 디렉토리 생성
            Path outputPath = Path.of(outputFilePath);
            Files.createDirectories(outputPath.getParent());
            
            FFmpeg ffmpeg = new FFmpeg();

            FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true);

            fileList.forEach(builder::addInput);

            String filterInput = IntStream.range(0, fileList.size())
                .mapToObj(i -> "[" + i + ":a]")
                .reduce("", String::concat);
            String complexFilter = filterInput + "concat=n=" + fileList.size() + ":v=0:a=1[out]";

            builder.setComplexFilter(complexFilter)
                .addOutput(outputFilePath)
                .addExtraArgs("-map", "[out]")
                .addExtraArgs("-c:a", "aac")
                .addExtraArgs("-b:a", "192k")
                .addExtraArgs("-movflags", "+faststart")
                .done();

            ffmpeg.run(builder);
            
            // 출력 파일 생성 확인
            if (!Files.exists(outputPath) || Files.size(outputPath) == 0) {
                throw new RuntimeException("FFmpeg 실행 후 출력 파일이 생성되지 않았거나 비어있습니다.");
            }
            
            log.info("FFmpeg 머지 완료. 출력 파일 크기: {} bytes", Files.size(outputPath));
            
        } catch (IOException e) {
            log.error("FFmpeg 머지 중 IO 오류 발생. 입력 파일들: {}", fileList, e);
            throw new RuntimeException("FFmpeg 머지에서 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("FFmpeg 머지 중 예상치 못한 오류 발생. 입력 파일들: {}", fileList, e);
            throw new RuntimeException("FFmpeg 머지에서 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public Resource getUrlResource(Path path) {
        try {
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("잘못된 파일 경로: " + path, e);
        }
    }

    public void deleteDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            log.warn("삭제 시도한 디렉토리가 존재하지 않습니다: {}", directoryPath);
            return;
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("경로가 디렉토리가 아닙니다: " + directoryPath);
        }

        try {
            FileUtils.cleanDirectory(directory);
            if (!directory.delete()) {
                throw new IOException("디렉토리 삭제 실패: " + directoryPath);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("디렉토리 삭제 중 오류 발생", e);
        }
    }
}
