package com.example.cashwalk.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUploadUtil {

    // 실행 경로 기준의 외부 디렉토리에 저장
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public static String saveFile(MultipartFile file) {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        File dest = new File(UPLOAD_DIR + filename);
        try {
            file.transferTo(dest);  // 실제 저장
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        // 클라이언트에게 줄 URL 경로 (나중에 정적 경로 설정 필요)
        return "/uploads/" + filename;
    }
}

