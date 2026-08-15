package com.example.couplead.common.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;

@Service
public class FileStorageService {
    private static final Path PROFILE_DIR = Paths.get(
            "uploads",
            "profile");

    public String saveProfileImage(
            MultipartFile file) {
        validateImage(
                file);

        try {
            Files.createDirectories(
                    PROFILE_DIR);

            String originalFilename = file.getOriginalFilename();

            String extension = getExtension(
                    originalFilename);

            String filename = UUID.randomUUID()
                    + extension;

            Path target = PROFILE_DIR.resolve(
                    filename);

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/profile/"
                    + filename;

        } catch (IOException e) {
            throw new RuntimeException(
                    "프로필 이미지 저장에 실패했습니다.",
                    e);
        }
    }

    private void validateImage(
            MultipartFile file) {
        if (file == null ||
                file.isEmpty()) {
            throw new IllegalArgumentException(
                    "이미지 파일이 없습니다.");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !contentType.startsWith(
                        "image/")) {
            throw new IllegalArgumentException(
                    "이미지 파일만 업로드할 수 있습니다.");
        }

        /*
         * 최대 5MB
         */
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "프로필 이미지는 5MB 이하만 가능합니다.");
        }
    }

    private String getExtension(
            String filename) {
        if (filename == null) {
            return "";
        }

        int index = filename.lastIndexOf(
                '.');

        if (index < 0) {
            return "";
        }

        return filename.substring(
                index);
    }
}