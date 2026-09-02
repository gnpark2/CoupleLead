package com.example.couplead.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.domain.dto.request.DeviceRegisterRequest;
import com.example.couplead.domain.entity.DeviceInstallation;
import com.example.couplead.domain.repository.DeviceInstallationRepository;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceServiceImpl
        implements DeviceService {

    private final DeviceInstallationRepository deviceInstallationRepository;

    private final UserRepository userRepository;

    @Override
    public void register(
            Long userId,
            DeviceRegisterRequest request) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new CustomException(
                                ErrorCode.USER_NOT_FOUND));

        DeviceInstallation device = deviceInstallationRepository
                .findByFid(
                        request.fid())
                .orElseGet(
                        () -> DeviceInstallation
                                .builder()
                                .user(user)
                                .fid(
                                        request.fid())
                                .fcmToken(
                                        request.fcmToken())
                                .platform(
                                        request.platform())
                                .build());

        /*
         * 앱 재로그인 등으로
         * 다른 사용자와 연결됐을 수도 있으므로
         * 현재 사용자로 갱신
         */
        device.updateOwner(
                user,
                request.fcmToken(),
                request.platform());

        deviceInstallationRepository
                .save(device);
    }

    @Override
    public void unregister(
            Long userId,
            String fid) {

        deviceInstallationRepository
                .findByFid(fid)
                .filter(
                        device -> device
                                .getUser()
                                .getId()
                                .equals(userId))
                .ifPresent(
                        deviceInstallationRepository::delete);
    }
}