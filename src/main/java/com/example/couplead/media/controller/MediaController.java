package com.example.couplead.media.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.media.dto.request.MediaCallActionRequest;
import com.example.couplead.media.dto.response.MediaInviteResponse;
import com.example.couplead.media.dto.response.MediaTokenResponse;
import com.example.couplead.media.service.MediaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController {

        private final MediaService mediaService;

        @PostMapping("/token")
        public ApiResponse<MediaTokenResponse> createToken(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                return ApiResponse.success(
                                mediaService.createToken(
                                                userDetails
                                                                .getUser()
                                                                .getId()));
        }

        @PostMapping("/invite")
        public ApiResponse<MediaInviteResponse> invite(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ApiResponse.success(
                                mediaService.invite(
                                                userDetails
                                                                .getUser()
                                                                .getId()));
        }

        @PostMapping("/accept")
        public ApiResponse<Void> accept(
                        @AuthenticationPrincipal CustomUserDetails userDetails,

                        @Valid @RequestBody MediaCallActionRequest request) {
                mediaService.accept(
                                userDetails
                                                .getUser()
                                                .getId(),
                                request);

                return ApiResponse.success();
        }

        @PostMapping("/reject")
        public ApiResponse<Void> reject(
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        @Valid @RequestBody MediaCallActionRequest request) {
                mediaService.reject(userDetails.getUser().getId(), request);
                return ApiResponse.success();
        }

        @PostMapping("/leave")
        public ApiResponse<Void> leave(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                mediaService.leave(userDetails.getUser().getId());

                return ApiResponse.success(null);
        }

}