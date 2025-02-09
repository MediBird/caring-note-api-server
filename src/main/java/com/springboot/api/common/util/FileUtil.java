package com.springboot.api.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUtil {

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload_", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }
}
