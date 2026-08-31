package com.example.couplead.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.couplead.common.file.S3Properties;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(
            S3Properties properties) {

        return S3Client.builder()
                .region(
                        Region.of(
                                properties.region()))
                .build();
    }
}