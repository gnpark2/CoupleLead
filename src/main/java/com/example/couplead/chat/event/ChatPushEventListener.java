package com.example.couplead.chat.event;

import com.example.couplead.chat.kafka.ChatPushProducer;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatPushEventListener {

    private final CoupleMemberRepository coupleMemberRepository;

    private final ChatPushProducer chatPushProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(
            ChatMessageCommittedEvent event) {

        Long coupleId = event.coupleId();

        Long senderId = event.message()
                .senderId();

        /*
         * 해당 커플의 사용자 두 명 조회
         */
        List<CoupleMember> members = coupleMemberRepository
                .findByCoupleIdWithUser(
                        coupleId);

        /*
         * 메시지를 보낸 사람이 아닌
         * 상대방을 찾는다.
         */
        CoupleMember receiver = members.stream()
                .filter(
                        member -> !member
                                .getUser()
                                .getId()
                                .equals(
                                        senderId))
                .findFirst()
                .orElse(null);

        if (receiver == null) {

            log.warn(
                    "[CHAT PUSH] "
                            + "수신자를 찾을 수 없음 "
                            + "coupleId={} "
                            + "senderId={}",
                    coupleId,
                    senderId);

            return;
        }

        Long receiverId = receiver
                .getUser()
                .getId();

        ChatPushEvent pushEvent = new ChatPushEvent(
                coupleId,
                senderId,
                receiverId,
                event.message()
                        .senderNickname(),
                event.message()
                        .type(),
                event.message()
                        .content());

        chatPushProducer.send(
                pushEvent);
    }
}