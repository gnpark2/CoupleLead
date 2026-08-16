package com.example.couplead.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;

import com.example.couplead.chat.domain.Message;
import com.example.couplead.chat.dto.response.ChatHistoryPageResponse;
import com.example.couplead.chat.dto.response.ChatHistoryResponse;
import com.example.couplead.chat.event.ChatReadCommittedEvent;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.event.dto.ChatReadEvent;
import com.example.couplead.event.producer.WidgetRefreshProducer;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;
import com.example.couplead.widget.service.WidgetCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {
    private final UserRepository userRepository;
    private final CoupleMemberRepository coupleMemberRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ChatHistoryPageResponse getMessages(
            Long userId,
            Long coupleId,
            Long beforeMessageId,
            int size) {
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

        Couple couple = member.getCouple();

        // 다른 커플의 채팅 접근 방지
        if (!couple.getId().equals(coupleId)) {
            throw new CustomException(
                    ErrorCode.COUPLE_NOT_FOUND);
        }

        // 최소/최대 크기 제한
        int pageSize = Math.max(
                1,
                Math.min(size, 100));

        /*
         * hasMore 판단을 위해 요청 크기보다 하나 더 가져온다.
         * size=50이면 실제 DB 조회는 51개.
         */
        PageRequest pageable = PageRequest.of(
                0,
                pageSize + 1);

        List<Message> result;

        if (beforeMessageId == null) {

            /*
             * 최초 진입:
             * 가장 최근 메시지부터 조회
             */
            result = messageRepository
                    .findByCoupleOrderByIdDesc(
                            couple,
                            pageable);

        } else {

            /*
             * 위로 스크롤:
             * 현재 가장 오래된 messageId보다 이전 메시지를 조회
             */
            result = messageRepository
                    .findByCoupleAndIdLessThanOrderByIdDesc(
                            couple,
                            beforeMessageId,
                            pageable);
        }

        boolean hasMore = result.size() > pageSize;


        // hasMore 판단용으로 가져온 마지막 한 개 제거

        List<Message> pageMessages = new ArrayList<>(
                hasMore
                        ? result.subList(
                                0,
                                pageSize)
                        : result);

        /*
         * DB에서는 최신 → 과거 순으로 가져왔지만
         * Flutter 채팅창에서는
         * 과거
         * ↓
         * 최신
         * 순으로 표시해야 하므로 뒤집는다.
         */
        Collections.reverse(
                pageMessages);

        List<ChatHistoryResponse> messages = pageMessages
                .stream()
                .map(
                        message -> new ChatHistoryResponse(
                                message.getId(),
                                message.getSender()
                                        .getId(),
                                message.getSender()
                                        .getNickname(),
                                message.getType(),
                                message.getContent(),
                                message.getSentAt(),
                                message.getReadAt()))
                .toList();


        // 현재 페이지의 가장 오래된 메시지 ID.

        Long nextCursor = pageMessages.isEmpty()
                ? null
                : pageMessages
                        .getFirst()
                        .getId();

        return new ChatHistoryPageResponse(
                messages,
                nextCursor,
                hasMore);
    }

    @Override
    @Transactional
    public void markAsRead(
            Long coupleId,
            Long userId) {
        LocalDateTime readAt = LocalDateTime.now();

        log.info(
                "[CHAT READ] START coupleId={}, readerId={}",
                coupleId,
                userId);

        int updated = messageRepository.markAsRead(
                coupleId,
                userId,
                readAt);

        log.info(
                "[CHAT READ] UPDATED coupleId={}, readerId={}, count={}",
                coupleId,
                userId,
                updated);

        long unreadCount = messageRepository
                .countByCoupleIdAndSenderIdNotAndReadAtIsNull(
                        coupleId,
                        userId);

        log.info(
                "[CHAT READ] AFTER coupleId={}, readerId={}, unreadCount={}",
                coupleId,
                userId,
                unreadCount);

        if (updated <= 0) {
            return;
        }

        ChatReadEvent readEvent = new ChatReadEvent(
                coupleId,
                userId,
                readAt);

        eventPublisher.publishEvent(
                new ChatReadCommittedEvent(
                        readEvent));
    }
}
