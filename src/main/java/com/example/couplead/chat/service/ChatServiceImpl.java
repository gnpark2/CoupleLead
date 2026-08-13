package com.example.couplead.chat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.chat.dto.response.ChatHistoryResponse;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.common.exception.CustomException;
import com.example.couplead.common.exception.ErrorCode;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.event.dto.ChatReadEvent;
import com.example.couplead.event.producer.ChatReadEventProducer;
import com.example.couplead.event.producer.WidgetRefreshProducer;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {
    private final UserRepository userRepository;
    private final CoupleMemberRepository coupleMemberRepository;
    private final MessageRepository messageRepository;
    private final ChatReadEventProducer chatReadEventProducer;
    private final WidgetRefreshProducer widgetRefreshProducer;

    @Override
    public List<ChatHistoryResponse> getMessages(Long userId, Long coupleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        CoupleMember member = coupleMemberRepository.findByUser(user)
            .orElseThrow(() -> new CustomException(ErrorCode.COUPLE_NOT_FOUND));

        Couple couple = member.getCouple();

        if (!couple.getId().equals(coupleId)) {
            throw new CustomException(ErrorCode.COUPLE_NOT_FOUND);
        }

        return messageRepository
            .findByCoupleOrderBySentAtAsc(couple)
            .stream()
            .map(message -> new ChatHistoryResponse(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getNickname(),
                message.getContent(),
                message.getSentAt(),
                message.getReadAt()
            ))
            .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long coupleId) {
        LocalDateTime now = LocalDateTime.now();
        int updated = messageRepository.markAsRead(coupleId, userId, now);

        if (updated > 0) {
            chatReadEventProducer.publish(
                new ChatReadEvent(coupleId, userId, now)
            );

            widgetRefreshProducer.publish(coupleId, "CHAT_READ");
        }
    }
}
