package com.example.couplead.chat.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.couplead.common.file.S3Properties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatImageStorageService {

        private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
                        "jpg",
                        "jpeg",
                        "png",
                        "webp");

        private final S3Client s3Client;

        private final S3Properties s3Properties;

        public String save(
                        MultipartFile file) {

                if (file == null ||
                                file.isEmpty()) {

                        throw new IllegalArgumentException(
                                        "이미지 파일이 비어 있습니다.");
                }

                String extension = getExtension(
                                file.getOriginalFilename());

                if (!ALLOWED_EXTENSIONS.contains(
                                extension)) {

                        throw new IllegalArgumentException(
                                        "지원하지 않는 이미지 형식입니다.");
                }

                String key = "chat/"
                                + UUID.randomUUID()
                                + "."
                                + extension;

                try {
                        PutObjectRequest request = PutObjectRequest.builder()
                                        .bucket(
                                                        s3Properties.bucket())
                                        .key(key)
                                        .contentType(
                                                        resolveContentType(
                                                                        file,
                                                                        extension))
                                        .build();

                        s3Client.putObject(
                                        request,
                                        RequestBody.fromInputStream(
                                                        file.getInputStream(),
                                                        file.getSize()));

                        log.info(
                                        "채팅 이미지 S3 업로드 완료: {}",
                                        key);

                        /*
                         * 전체 URL을 DB에 저장하지 않고
                         * Object Key만 저장.
                         */
                        return key;

                } catch (IOException e) {

                        throw new IllegalStateException(
                                        "채팅 이미지 S3 업로드에 실패했습니다.",
                                        e);
                }
        }

        public List<String> saveAll(
                        List<MultipartFile> files) {

                return files.stream()
                                .map(this::save)
                                .toList();
        }

        public void delete(
                        String key) {

                if (key == null ||
                                key.isBlank()) {
                        return;
                }

                /*
                 * 기존 로컬 이미지와
                 * 새 S3 key 구분.
                 */
                if (!key.startsWith(
                                "chat/")) {

                        log.debug(
                                        "S3 이미지가 아니므로 삭제 건너뜀: {}",
                                        key);

                        return;
                }

                try {
                        DeleteObjectRequest request = DeleteObjectRequest.builder()
                                        .bucket(
                                                        s3Properties.bucket())
                                        .key(key)
                                        .build();

                        s3Client.deleteObject(
                                        request);

                        log.info(
                                        "채팅 이미지 S3 삭제: {}",
                                        key);

                } catch (Exception e) {

                        log.warn(
                                        "채팅 이미지 S3 삭제 실패: {}",
                                        key,
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

        private String resolveContentType(
                        MultipartFile file,
                        String extension) {

                if (file.getContentType() != null &&
                                !file.getContentType()
                                                .isBlank()) {

                        return file.getContentType();
                }

                return switch (extension) {

                        case "jpg", "jpeg" ->
                                "image/jpeg";

                        case "png" ->
                                "image/png";

                        case "webp" ->
                                "image/webp";

                        default ->
                                "application/octet-stream";
                };
        }
}