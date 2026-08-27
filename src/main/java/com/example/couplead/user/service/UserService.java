package com.example.couplead.user.service;

import com.example.couplead.auth.dto.request.SignupRequest;
import com.example.couplead.auth.dto.response.SignupResponse;
import com.example.couplead.user.dto.request.UpdateLocationRequest;
import com.example.couplead.user.dto.request.UpdateProfileRequest;
import com.example.couplead.user.dto.response.UserProfileResponse;

public interface UserService {
        SignupResponse signup(SignupRequest request);

        void withdraw(Long userId);

        UserProfileResponse updateProfile(
                        Long userId,
                        UpdateProfileRequest request);

        UserProfileResponse updateProfileImage(
                        Long userId,
                        String profileImage);

        void deleteProfileImage(
                        Long userId);

        void updateLocation(Long userId, UpdateLocationRequest request);
}
