package com.example.couplead.common.file;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController {

    private final S3FileService s3FileService;

    @GetMapping("/chat/{filename}")
    public ResponseEntity<byte[]> getChatImage(
            @PathVariable String filename) {

        String key = "chat/"
                + filename;

        S3FileService.S3File file = s3FileService.get(
                key);

        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;

        if (file.contentType() != null) {
            contentType = MediaType.parseMediaType(
                    file.contentType());
        }

        return ResponseEntity
                .ok()
                .contentType(
                        contentType)
                .body(
                        file.bytes());
    }
}