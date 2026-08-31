package com.example.couplead.common.file;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;

    private final S3Properties s3Properties;

    public S3File get(
            String key) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(
                        s3Properties.bucket())
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                request);

        return new S3File(
                response.asByteArray(),
                response.response()
                        .contentType());
    }

    public record S3File(
            byte[] bytes,
            String contentType) {
    }
}