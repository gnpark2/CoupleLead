package com.example.couplead.anniversary.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.anniversary.dto.request.CreateAnniversaryRequest;
import com.example.couplead.anniversary.dto.response.AnniversaryResponse;
import com.example.couplead.anniversary.service.AnniversaryService;
import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/anniversaries")
public class AnniversaryController {
    private final AnniversaryService anniversaryService;

    @PostMapping
    public ApiResponse<AnniversaryResponse> create(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CreateAnniversaryRequest request) {
        return ApiResponse.success(
            anniversaryService.create(
                userDetails.getUser().getId(),
                request
            )
        );
    }

    @GetMapping
    public ApiResponse<List<AnniversaryResponse>> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(
            anniversaryService.getAll(
                userDetails.getUser().getId()
            )
        );
    }

    @DeleteMapping("/{anniversaryId}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long anniversaryId) {
        anniversaryService.delete(
            userDetails.getUser().getId(),
            anniversaryId
        );

        return ApiResponse.success();
    }
}
