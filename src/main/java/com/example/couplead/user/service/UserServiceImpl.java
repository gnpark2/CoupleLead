package com.example.couplead.user.service;

import com.example.couplead.auth.dto.request.SignupRequest;
import com.example.couplead.auth.dto.response.SignupResponse;
import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.user.domain.Provider;
import com.example.couplead.user.domain.Role;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.dto.request.UpdateLocationRequest;
import com.example.couplead.user.dto.request.UpdateProfileRequest;
import com.example.couplead.user.dto.response.UserProfileResponse;
import com.example.couplead.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname());
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(
            Long userId,
            UpdateProfileRequest request) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.USER_NOT_FOUND));

        user.updateNickname(
                request.nickname());

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage());
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfileImage(
            Long userId,
            String profileImage) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.USER_NOT_FOUND));

        user.updateProfileImage(
                profileImage);

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage());
    }

    @Override
    public void updateLocation(Long userId, UpdateLocationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateLocation(
                request.country(),
                request.city(),
                request.timezone(),
                request.latitude(),
                request.longitude());
    }
}
