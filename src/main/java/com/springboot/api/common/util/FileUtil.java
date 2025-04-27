package com.springboot.api.common.util;

import com.springboot.api.common.properties.FfmpegProperties;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

}
