package com.example.couplead.user.service;

import com.example.couplead.auth.dto.request.SignupRequest;
import com.example.couplead.auth.dto.response.SignupResponse;
import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.user.domain.Provider;
import com.example.couplead.user.domain.Role;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.dto.request.UpdateLocationRequest;
import com.example.couplead.user.dto.request.UpdateProfileRequest;
import com.example.couplead.user.dto.response.UserProfileResponse;
import com.example.couplead.user.event.ProfileImageReplacedEvent;
import com.example.couplead.user.event.ProfileUpdatedEvent;
import com.example.couplead.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final CoupleMemberRepository coupleMemberRepository;
        private final ApplicationEventPublisher eventPublisher;

        private Long getCoupleId(User user) {
                return coupleMemberRepository
                                .findByUser(user)
                                .map(member -> member.getCouple()
                                                .getId())
                                .orElse(null);
        }

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

                if (userRepository.existsByNicknameAndIdNot(
                                request.nickname(),
                                userId)) {
                        throw new CustomException(
                                        ErrorCode.DUPLICATE_NICKNAME);
                }

                user.updateNickname(
                                request.nickname());

                Long coupleId = getCoupleId(
                                user);

                if (coupleId != null) {
                        eventPublisher.publishEvent(
                                        new ProfileUpdatedEvent(
                                                        coupleId));
                }

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
                        String newProfileImage) {
                User user = userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.USER_NOT_FOUND));

                String oldProfileImage = user.getProfileImage();

                user.updateProfileImage(
                                newProfileImage);

                /*
                 * DB commit 후 기존 파일 삭제
                 */
                if (oldProfileImage != null &&
                                !oldProfileImage.equals(
                                                newProfileImage)) {
                        eventPublisher.publishEvent(
                                        new ProfileImageReplacedEvent(
                                                        oldProfileImage));
                }

                /*
                 * 상대방 Widget refresh
                 */
                Long coupleId = getCoupleId(user);

                if (coupleId != null) {
                        eventPublisher.publishEvent(
                                        new ProfileUpdatedEvent(
                                                        coupleId));
                }

                return new UserProfileResponse(
                                user.getId(),
                                user.getEmail(),
                                user.getNickname(),
                                user.getProfileImage());
        }

        @Override
        @Transactional
        public void deleteProfileImage(
                        Long userId) {
                User user = userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.USER_NOT_FOUND));

                String oldProfileImage = user.getProfileImage();

                /*
                 * 이미 기본 이미지 상태면
                 * 아무것도 할 필요 없음
                 */
                if (oldProfileImage == null ||
                                oldProfileImage.isBlank()) {
                        return;
                }

                /*
                 * DB에서는 null 처리
                 */
                user.removeProfileImage();

                /*
                 * commit 성공 후
                 * 실제 파일 삭제
                 */
                eventPublisher.publishEvent(
                                new ProfileImageReplacedEvent(
                                                oldProfileImage));

                /*
                 * 상대방에게 변경 알림
                 */
                Long coupleId = getCoupleId(user);

                if (coupleId != null) {
                        eventPublisher.publishEvent(
                                        new ProfileUpdatedEvent(
                                                        coupleId));
                }
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
