package com.springboot.api.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.api.common.dto.ByteArrayMultipartFile;
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

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload_", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    private String saveMultipartFile(@NotNull MultipartFile multipartFile, @NotNull String saveFilePath)
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

        log.debug(hashedFilename);

        Path filePath = dirPath.resolve(hashedFilename);

        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return hashedFilename;

    }

    public MultipartFile convertWebmToMp4(MultipartFile multipartFile, String originFilePath, String convertFilePath)
            throws IOException {

        String savedMultipartFileName = saveMultipartFile(multipartFile, originFilePath);
        String convertedMultipartFileName = savedMultipartFileName.replace(".webm", ".mp4");

        FFmpeg ffmpeg = new FFmpeg(ffmpegProperties.getPath());

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(originFilePath + savedMultipartFileName)
                .overrideOutputFiles(true)
                .addOutput(convertFilePath + convertedMultipartFileName) // 출력 파일 설정
                .setFormat("mp4") // 출력 포맷 설정
                .setAudioCodec("aac") // 오디오 코덱 설정 (AAC)
                .setAudioBitRate(192000) // 오디오 비트레이트 설정 (192kbps)
                .done();

        ffmpeg.run(builder);

        File mp4File = new File(convertFilePath + convertedMultipartFileName);
        byte[] fileBytes = Files.readAllBytes(mp4File.toPath());
        MultipartFile convertedMultipartFile = new ByteArrayMultipartFile("media", convertedMultipartFileName,
                "audio/mp4", fileBytes);

        Files.delete(Path.of(originFilePath + savedMultipartFileName));
        Files.delete(Path.of(convertFilePath + convertedMultipartFileName));

        return convertedMultipartFile;
    }

}
