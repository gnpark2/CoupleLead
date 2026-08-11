package com.example.couplead.couple.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.domain.CoupleRole;
import com.example.couplead.couple.domain.CoupleStatus;
import com.example.couplead.couple.dto.request.ConnectRequest;
import com.example.couplead.couple.dto.response.CoupleResponse;
import com.example.couplead.couple.dto.response.InviteCodeResponse;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.couple.repository.CoupleRepository;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CoupleServiceImpl implements CoupleService {
    private static final String PREFIX = "invite:";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final RedisTemplate<String, String> redisTemplate;
    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;
    private final CoupleMemberRepository coupleMemberRepository;

    @Override
    public InviteCodeResponse createInviteCode(Long userId) {
        String code = generateCode();

        redisTemplate.opsForValue().set(
                PREFIX + code,
                String.valueOf(userId),
                24,
                TimeUnit.HOURS);

        return new InviteCodeResponse(code);
    }

    @Override
    public void connect(Long userId, ConnectRequest request) {
        String key = PREFIX + request.inviteCode();
        String ownerIdString = redisTemplate.opsForValue().get(key);

        if (ownerIdString == null) {
            throw new CustomException(ErrorCode.INVALID_INVITE_CODE);
        }

        Long ownerId = Long.parseLong(ownerIdString);

        if (ownerId.equals(userId)) {
            throw new CustomException(ErrorCode.CANNOT_CONNECT_SELF);
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User partner = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (coupleMemberRepository.existsByUser(owner) || coupleMemberRepository.existsByUser(partner)) {
            throw new CustomException(ErrorCode.ALREADY_IN_COUPLE);
        }

        Couple couple = coupleRepository.save(
                Couple.builder()
                        .connectedAt(LocalDate.now())
                        .status(CoupleStatus.ACTIVE)
                        .build());

        coupleMemberRepository.save(
                CoupleMember.builder()
                        .couple(couple)
                        .user(owner)
                        .role(CoupleRole.FIRST)
                        .build());

        coupleMemberRepository.save(
                CoupleMember.builder()
                        .couple(couple)
                        .user(partner)
                        .role(CoupleRole.SECOND)
                        .build());

        redisTemplate.delete(key);
    }

    @Override
    @Transactional(readOnly = true)
    public CoupleResponse getMyCouple(Long userId) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        CoupleMember coupleMember = coupleMemberRepository.findByUser(me)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPLE_NOT_FOUND));

        Couple couple = coupleMember.getCouple();

        CoupleMember partnerMember = coupleMemberRepository.findByCouple(couple)
                .stream()
                .filter(member -> !member.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.COUPLE_NOT_FOUND));

        User partner = partnerMember.getUser();

        Long daysTogether = null;

        if (couple.getConnectedAt() != null) {

            daysTogether = ChronoUnit.DAYS.between(
                    couple.getConnectedAt(),
                    LocalDate.now());
        }

        return new CoupleResponse(
                couple.getId(),
                partner.getId(),
                partner.getNickname(),
                partner.getProfileImage(),
                partner.getCountry(),
                partner.getTimezone(),
                couple.getConnectedAt(),
                daysTogether
            );
    }

    @Override
    public void disconnect(Long userId) {
        throw new UnsupportedOperationException();
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
