package com.example.couplead.chat.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;

@Service
public class ChatImageStorageService {

    private static final Path CHAT_DIRECTORY = Paths.get(
            "uploads",
            "chat");

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp");

    public String save(
            MultipartFile file) {
        if (file == null ||
                file.isEmpty()) {
            throw new IllegalArgumentException(
                    "이미지 파일이 비어 있습니다.");
        }

        String originalFilename = file.getOriginalFilename();

        String extension = getExtension(
                originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(
                extension)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 이미지 형식입니다.");
        }

        try {
            Files.createDirectories(
                    CHAT_DIRECTORY);

            String filename = UUID.randomUUID()
                    + "."
                    + extension;

            Path target = CHAT_DIRECTORY
                    .resolve(filename)
                    .normalize();

            Files.copy(
                    file.getInputStream(),
                    target);

            return "/uploads/chat/"
                    + filename;

        } catch (IOException e) {
            throw new RuntimeException(
                    "채팅 이미지 저장에 실패했습니다.",
                    e);
        }
    }

    private String getExtension(
            String filename) {
        if (filename == null ||
                !filename.contains(".")) {
            throw new IllegalArgumentException(
                    "파일 확장자가 없습니다.");
        }

        return filename
                .substring(
                        filename.lastIndexOf('.') + 1)
                .toLowerCase();
    }
}