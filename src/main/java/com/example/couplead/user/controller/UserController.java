package com.example.couplead.user.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.file.FileStorageService;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.user.dto.request.UpdateLocationRequest;
import com.example.couplead.user.dto.request.UpdateProfileRequest;
import com.example.couplead.user.dto.response.UserMeResponse;
import com.example.couplead.user.dto.response.UserProfileResponse;
import com.example.couplead.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
        private final UserService userService;
        private final FileStorageService fileStorageService;

        @GetMapping("/me")
        public ApiResponse<UserMeResponse> me(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                return ApiResponse.success(
                                UserMeResponse.from(userDetails.getUser()));
        }

        @PatchMapping("/me/profile")
        public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
                        @AuthenticationPrincipal CustomUserDetails userDetails,

                        @Valid @RequestBody UpdateProfileRequest request) {

                UserProfileResponse response = userService.updateProfile(
                                userDetails.getUser().getId(),
                                request);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                response));
        }

        @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfileImage(
                        @AuthenticationPrincipal CustomUserDetails userDetails,

                        @RequestPart("file") MultipartFile file) {

                String profileImage = fileStorageService
                                .saveProfileImage(
                                                file);

                UserProfileResponse response = userService
                                .updateProfileImage(
                                                userDetails.getUser().getId(),
                                                profileImage);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                response));
        }

        @DeleteMapping("/me/profile-image")
        public ApiResponse<Void> deleteProfileImage(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                userService.deleteProfileImage(
                                userDetails.getUser().getId());

                return ApiResponse.success(
                                null);
        }

        @PatchMapping("/me/location")
        public ApiResponse<Void> updateLocation(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @Valid @RequestBody UpdateLocationRequest request) {
                userService.updateLocation(
                                userDetails.getUser().getId(),
                                request);

                return ApiResponse.success();
        }

        // @PostMapping("/me/location-debug")
        // public ApiResponse<Map<String, Object>> debugLocation(
        // @RequestBody Map<String, Object> body) {
        // System.out.println("BODY = " + body);

        // return ApiResponse.success(body);
        // }

}
