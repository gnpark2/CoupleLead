package com.example.couplead.media.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.couple.repository.CoupleRepository;
import com.example.couplead.media.dto.request.MediaCallActionRequest;
import com.example.couplead.media.dto.response.MediaInviteResponse;
import com.example.couplead.media.dto.response.MediaTokenResponse;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaServiceImpl
                implements MediaService {

        private final UserRepository userRepository;
        private final CoupleMemberRepository coupleMemberRepository;
        private final SimpMessagingTemplate messagingTemplate;
        private final CoupleRepository coupleRepository;

        @Value("${livekit.url}")
        private String livekitUrl;

        @Value("${livekit.api-key}")
        private String apiKey;

        @Value("${livekit.api-secret}")
        private String apiSecret;

        @Override
        public MediaTokenResponse createToken(
                        Long userId) {
                User user = userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.USER_NOT_FOUND));

                CoupleMember member = coupleMemberRepository
                                .findByUser(user)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                Long coupleId = member.getCouple()
                                .getId();

                String roomName = "couple-" + coupleId;

                String identity = "user-" + userId;

                AccessToken accessToken = new AccessToken(
                                apiKey,
                                apiSecret);

                accessToken.setIdentity(
                                identity);

                accessToken.addGrants(
                                new RoomJoin(true),
                                new RoomName(roomName));

                String token = accessToken.toJwt();

                return new MediaTokenResponse(
                                livekitUrl,
                                token,
                                roomName);
        }

        private User getPartner(
                        User me) {
                CoupleMember myMember = coupleMemberRepository
                                .findByUser(me)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                return coupleMemberRepository
                                .findByCoupleWithUser(
                                                myMember.getCouple())
                                .stream()
                                .map(CoupleMember::getUser)
                                .filter(
                                                user -> !user.getId()
                                                                .equals(me.getId()))
                                .findFirst()
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));
        }

        @Override
        public MediaInviteResponse invite(
                        Long userId) {
                User me = userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.USER_NOT_FOUND));

                User partner = getPartner(me);

                String callId = UUID.randomUUID()
                                .toString();

                Map<String, Object> payload = Map.of(
                                "type",
                                "MEDIA_INVITE",

                                "callId",
                                callId,

                                "callerUserId",
                                me.getId(),

                                "callerNickname",
                                me.getNickname());

                messagingTemplate.convertAndSend(
                                "/topic/media/user/"
                                                + partner.getId(),
                                payload);

                return new MediaInviteResponse(
                                callId);
        }

        @Override
        public void accept(
                        Long userId,
                        MediaCallActionRequest request) {
                User me = userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.USER_NOT_FOUND));

                User partner = getPartner(me);

                /*
                 * 요청에 들어있는 callerUserId가
                 * 실제 내 상대방인지 검증
                 */
                if (!partner.getId()
                                .equals(
                                                request.callerUserId())) {
                        throw new CustomException(
                                        ErrorCode.ACCESS_DENIED);
                }

                Map<String, Object> payload = Map.of(
                                "type",
                                "MEDIA_ACCEPTED",

                                "callId",
                                request.callId(),

                                "userId",
                                me.getId());

                /*
                 * 전화를 건 사람에게 수락 알림
                 */
                messagingTemplate.convertAndSend(
                                "/topic/media/user/"
                                                + partner.getId(),
                                payload);
        }

        @Override
        public void reject(
                        Long userId,
                        MediaCallActionRequest request) {
                User me = userRepository
                                .findById(userId)
                                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                User partner = getPartner(me);

                if (!partner.getId().equals(request.callerUserId())) {
                        throw new CustomException(ErrorCode.ACCESS_DENIED);
                }

                Map<String, Object> payload = Map.of(
                                "type",
                                "MEDIA_REJECTED",

                                "callId",
                                request.callId(),

                                "userId",
                                me.getId());

                messagingTemplate.convertAndSend("/topic/media/user/" + partner.getId(), payload);

        }

        @Override
        @Transactional(readOnly = true)
        public void leave(Long userId) {
                User me = userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.USER_NOT_FOUND));

                /*
                 * 현재 유저의 CoupleMember 조회
                 */
                CoupleMember myMember = coupleMemberRepository
                                .findByUser(me)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                Couple couple = myMember.getCouple();

                /*
                 * 같은 커플의 멤버 전체 조회
                 */
                var members = coupleMemberRepository
                                .findByCoupleWithUser(couple);

                /*
                 * 나를 제외한 상대방 찾기
                 */
                CoupleMember partnerMember = members
                                .stream()
                                .filter(
                                                member -> !member
                                                                .getUser()
                                                                .getId()
                                                                .equals(userId))
                                .findFirst()
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                Long partnerUserId = partnerMember
                                .getUser()
                                .getId();

                /*
                 * 상대방에게 미디어 룸 종료 이벤트 전송
                 */
                messagingTemplate.convertAndSend(
                                "/topic/media/user/" + partnerUserId,
                                Map.of(
                                                "type", "MEDIA_LEFT",
                                                "userId", userId));
        }
}