package com.example.couplead.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;

import com.example.couplead.chat.domain.ChatAnnouncement;
import com.example.couplead.chat.domain.Message;
import com.example.couplead.chat.domain.MessageType;
import com.example.couplead.chat.dto.response.ChatAnnouncementResponse;
import com.example.couplead.chat.dto.response.ChatHistoryPageResponse;
import com.example.couplead.chat.dto.response.ChatHistoryResponse;
import com.example.couplead.chat.dto.response.ChatSearchPageResponse;
import com.example.couplead.chat.dto.response.ChatSearchResponse;
import com.example.couplead.chat.event.ChatAnnouncementChangedEvent;
import com.example.couplead.chat.event.ChatMessageDeletedEvent;
import com.example.couplead.chat.event.ChatMessageEditedEvent;
import com.example.couplead.chat.event.ChatReadCommittedEvent;
import com.example.couplead.chat.repository.ChatAnnouncementRepository;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.chat.repository.MessageSearchRepository;
import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.couple.repository.CoupleRepository;
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
        private final ChatAnnouncementRepository chatAnnouncementRepository;
        private final CoupleRepository coupleRepository;
        private final ChatSearchService chatSearchService;

        private ChatAnnouncementResponse toAnnouncementResponse(
                        ChatAnnouncement announcement) {
                return new ChatAnnouncementResponse(
                                announcement.getId(),
                                announcement.getMessage().getId(),

                                announcement.getCreatedBy().getId(),
                                announcement.getCreatedBy().getNickname(),

                                announcement.getMessage()
                                                .getSender()
                                                .getId(),

                                announcement.getMessage()
                                                .getSender()
                                                .getNickname(),

                                announcement.getContent(),

                                announcement.getMessage()
                                                .getSentAt(),

                                announcement.getCreatedAt());
        }

        @Override
        @Transactional(readOnly = true)
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
                                .map(message -> {

                                        Message reply = message.getReplyToMessage();

                                        return new ChatHistoryResponse(
                                                        message.getId(),
                                                        message.getSender().getId(),
                                                        message.getSender().getNickname(),
                                                        message.getType(),
                                                        message.getContent(),
                                                        message.getSentAt(),
                                                        message.getReadAt(),
                                                        message.isDeleted(),
                                                        message.getDeletedAt(),
                                                        message.isEdited(),
                                                        message.getEditedAt(),

                                                        reply == null
                                                                        ? null
                                                                        : reply.getId(),

                                                        reply == null
                                                                        ? null
                                                                        : reply.getSender().getNickname(),

                                                        reply == null
                                                                        ? null
                                                                        : reply.getType(),

                                                        reply == null
                                                                        ? null
                                                                        : reply.isDeleted()
                                                                                        ? "삭제된 메시지입니다."
                                                                                        : reply.getContent(),

                                                        // 추가
                                                        message.getClientMessageId());
                                })
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

        @Override
        @Transactional
        public void deleteMessage(
                        Long userId,
                        Long messageId) {
                Message message = messageRepository
                                .findById(messageId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.MESSAGE_NOT_FOUND));

                if (message.isDeleted()) {
                        return;
                }

                if (!message.getSender()
                                .getId()
                                .equals(userId)) {

                        throw new CustomException(
                                        ErrorCode.MESSAGE_DELETE_FORBIDDEN);
                }

                Long coupleId = message.getCouple()
                                .getId();

                MessageType type = message.getType();

                String imagePath = type == MessageType.IMAGE
                                ? message.getContent()
                                : null;

                Optional<ChatAnnouncement> announcement = chatAnnouncementRepository
                                .findByMessage(message);

                boolean wasAnnouncement = announcement.isPresent();

                announcement.ifPresent(
                                chatAnnouncementRepository::delete);

                message.deleteForEveryone();

                eventPublisher.publishEvent(
                                new ChatMessageDeletedEvent(
                                                coupleId,
                                                message.getId(),
                                                type,
                                                imagePath,
                                                message.getDeletedAt()));

                if (wasAnnouncement) {
                        eventPublisher.publishEvent(
                                        new ChatAnnouncementChangedEvent(
                                                        coupleId));
                }
        }

        @Override
        @Transactional
        public void editMessage(
                        Long userId,
                        Long messageId,
                        String content) {
                Message message = messageRepository
                                .findById(messageId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.MESSAGE_NOT_FOUND));

                /*
                 * 이미 삭제된 메시지는 수정 불가
                 */
                if (message.isDeleted()) {
                        throw new CustomException(
                                        ErrorCode.MESSAGE_NOT_FOUND);
                }

                /*
                 * 본인 메시지만 수정 가능
                 */
                if (!message.getSender()
                                .getId()
                                .equals(userId)) {

                        throw new CustomException(
                                        ErrorCode.MESSAGE_EDIT_FORBIDDEN);
                }

                /*
                 * TEXT 메시지만 수정 가능
                 */
                if (message.getType() != MessageType.TEXT) {

                        throw new CustomException(
                                        ErrorCode.MESSAGE_EDIT_NOT_ALLOWED);
                }

                String trimmed = content == null
                                ? ""
                                : content.trim();

                if (trimmed.isEmpty()) {
                        throw new CustomException(
                                        ErrorCode.INVALID_MESSAGE_CONTENT);
                }

                /*
                 * 기존 내용과 같으면 굳이 수정하지 않음
                 */
                if (message.getContent()
                                .equals(trimmed)) {
                        return;
                }

                message.editContent(
                                trimmed);

                eventPublisher.publishEvent(
                                new ChatMessageEditedEvent(
                                                message.getCouple().getId(),
                                                message.getId(),
                                                trimmed,
                                                message.getEditedAt()));
        }

        @Override
        @Transactional(readOnly = true)
        public ChatSearchPageResponse searchMessages(
                        Long userId,
                        Long coupleId,
                        String keyword,
                        boolean useNori,
                        int size,
                        LocalDateTime beforeSentAt,
                        Long beforeMessageId) {

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

                /*
                 * 다른 커플 채팅 검색 방지
                 */
                if (!couple.getId()
                                .equals(coupleId)) {

                        throw new CustomException(
                                        ErrorCode.COUPLE_NOT_FOUND);
                }

                String trimmed = keyword == null
                                ? ""
                                : keyword.trim();

                if (trimmed.isEmpty()) {
                        return new ChatSearchPageResponse(
                                        List.of(),
                                        null,
                                        null,
                                        false);
                }

                ChatSearchService.SearchResultPage page = chatSearchService.search(
                                coupleId,
                                trimmed,
                                useNori,
                                size,
                                beforeSentAt,
                                beforeMessageId);

                List<ChatSearchResponse> messages = page.messages()
                                .stream()
                                .map(
                                                document -> new ChatSearchResponse(
                                                                document.getMessageId(),
                                                                document.getSenderId(),
                                                                document.getSenderNickname(),
                                                                document.getContent(),
                                                                document.getSentAt()))
                                .toList();

                return new ChatSearchPageResponse(
                                messages,
                                page.nextSentAt(),
                                page.nextMessageId(),
                                page.hasMore());
        }

        @Override
        @Transactional
        public ChatAnnouncementResponse setAnnouncement(
                        Long userId,
                        Long coupleId,
                        Long messageId) {
                User user = userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.USER_NOT_FOUND));

                Couple couple = coupleRepository
                                .findById(coupleId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                /*
                 * 현재 로그인 사용자가
                 * 해당 커플 구성원인지 확인
                 */
                var member = coupleMemberRepository
                                .findByUser(user)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                if (!member.getCouple()
                                .getId()
                                .equals(coupleId)) {

                        throw new CustomException(
                                        ErrorCode.COUPLE_NOT_FOUND);
                }

                Message message = messageRepository
                                .findById(messageId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.MESSAGE_NOT_FOUND));

                /*
                 * 다른 채팅방 메시지를
                 * 공지할 수 없도록 방지
                 */
                if (!message.getCouple()
                                .getId()
                                .equals(coupleId)) {

                        throw new CustomException(
                                        ErrorCode.MESSAGE_NOT_FOUND);
                }

                if (message.isDeleted()) {
                        throw new CustomException(
                                        ErrorCode.MESSAGE_NOT_FOUND);
                }

                /*
                 * 일단 TEXT만 공지 가능
                 */
                if (message.getType() != MessageType.TEXT) {

                        throw new IllegalArgumentException(
                                        "텍스트 메시지만 공지할 수 있습니다.");
                }

                ChatAnnouncement announcement = chatAnnouncementRepository
                                .findByCouple(couple)
                                .orElseGet(
                                                () -> ChatAnnouncement
                                                                .builder()
                                                                .couple(couple)
                                                                .message(message)
                                                                .createdBy(user)
                                                                .content(
                                                                                message.getContent())
                                                                .build());

                if (announcement.getId() != null) {
                        announcement.update(
                                        message,
                                        user);
                }

                ChatAnnouncement saved = chatAnnouncementRepository.save(
                                announcement);

                eventPublisher.publishEvent(
                                new ChatAnnouncementChangedEvent(
                                                coupleId));

                return toAnnouncementResponse(
                                saved);
        }

        @Override
        @Transactional(readOnly = true)
        public Optional<ChatAnnouncementResponse> getAnnouncement(
                        Long userId,
                        Long coupleId) {

                Couple couple = coupleRepository
                                .findById(coupleId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                return chatAnnouncementRepository
                                .findByCouple(couple)
                                .map(
                                                this::toAnnouncementResponse);
        }

        @Override
        @Transactional
        public void removeAnnouncement(
                        Long userId,
                        Long coupleId) {
                User user = userRepository
                                .findById(userId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.USER_NOT_FOUND));

                Couple couple = coupleRepository
                                .findById(coupleId)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                /*
                 * 현재 사용자가 이 커플의 구성원인지 확인
                 */
                var coupleMember = coupleMemberRepository
                                .findByUser(user)
                                .orElseThrow(
                                                () -> new CustomException(
                                                                ErrorCode.COUPLE_NOT_FOUND));

                if (!coupleMember
                                .getCouple()
                                .getId()
                                .equals(coupleId)) {

                        throw new CustomException(
                                        ErrorCode.COUPLE_NOT_FOUND);
                }

                /*
                 * 현재 공지 조회
                 */
                ChatAnnouncement announcement = chatAnnouncementRepository
                                .findByCouple(couple)
                                .orElse(null);

                /*
                 * 이미 공지가 없으면 그냥 종료
                 */
                if (announcement == null) {
                        return;
                }

                /*
                 * 공지 삭제
                 */
                chatAnnouncementRepository.delete(
                                announcement);

                /*
                 * DB commit 이후
                 * 상대방 화면도 공지가 사라지도록
                 * WebSocket 이벤트 발생
                 */
                eventPublisher.publishEvent(
                                new ChatAnnouncementChangedEvent(
                                                coupleId));
        }
}
