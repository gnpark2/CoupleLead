package com.example.couplead.user.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfileImageStorageService {

    private static final Path PROFILE_DIRECTORY = Paths.get(
            "uploads",
            "profile");

    public void delete(
            String profileImage) {
        if (profileImage == null ||
                profileImage.isBlank()) {
            return;
        }

        /*
         * 외부 URL이면 로컬 파일이 아니므로
         * 삭제하지 않는다.
         */
        if (profileImage.startsWith("http://") ||
                profileImage.startsWith("https://")) {
            return;
        }

        /*
         * DB:
         * /uploads/profile/abc.jpg
         *
         * 실제:
         * uploads/profile/abc.jpg
         */
        String filename = profileImage.substring(
                profileImage.lastIndexOf('/') + 1);

        Path filePath = PROFILE_DIRECTORY
                .resolve(filename)
                .normalize();

        if (!filePath.startsWith(
                PROFILE_DIRECTORY.normalize())) {
            return;
        }

        try {
            boolean deleted = Files.deleteIfExists(
                    filePath);

            if (deleted) {
                log.info(
                        "기존 프로필 이미지 삭제: {}",
                        filePath);
            }
        } catch (IOException e) {
            /*
             * 이미지 파일 삭제 실패 때문에
             * 사용자 프로필 변경 자체를
             * 실패시키지는 않는다.
             */
            log.warn(
                    "프로필 이미지 파일 삭제 실패: {}",
                    filePath,
                    e);
        }
    }
}