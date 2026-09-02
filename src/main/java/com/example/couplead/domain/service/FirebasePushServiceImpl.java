package com.example.couplead.domain.service;

import com.example.couplead.domain.entity.DeviceInstallation;
import com.example.couplead.domain.repository.DeviceInstallationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebasePushServiceImpl
        implements FirebasePushService {

    private final DeviceInstallationRepository deviceInstallationRepository;

    @Override
    public void sendToUser(
            Long userId,
            String title,
            String body,
            Long coupleId,
            Long senderId) {

        List<DeviceInstallation> devices = deviceInstallationRepository
                .findAllByUserId(
                        userId);

        if (devices.isEmpty()) {
            log.debug(
                    "[FCM] 등록된 기기 없음 userId={}",
                    userId);

            return;
        }

        for (DeviceInstallation device : devices) {

            try {

                Message message = Message.builder()

                        .setNotification(
                                Notification.builder()
                                        .setTitle(title)
                                        .setBody(body)
                                        .build())

                        .putData(
                                "type",
                                "CHAT_MESSAGE")

                        .putData(
                                "coupleId",
                                String.valueOf(
                                        coupleId))

                        .putData(
                                "senderId",
                                String.valueOf(
                                        senderId))

                        .putData(
                                "senderNickname",
                                title)

                        /*
                         * 현재 실제 테스트에서
                         * 성공한 방식 유지
                         */
                        .setToken(
                                device.getFcmToken())

                        .build();

                String response = FirebaseMessaging
                        .getInstance()
                        .send(
                                message);

                log.info(
                        "[FCM] 발송 성공 "
                                + "userId={} "
                                + "fid={} "
                                + "response={}",
                        userId,
                        device.getFid(),
                        response);

            } catch (Exception e) {

                log.error(
                        "[FCM] 발송 실패 "
                                + "userId={} "
                                + "fid={}",
                        userId,
                        device.getFid(),
                        e);
            }
        }
    }
}