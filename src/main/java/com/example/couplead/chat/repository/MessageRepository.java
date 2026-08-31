package com.example.couplead.chat.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.couplead.chat.domain.Message;
import com.example.couplead.couple.domain.Couple;

public interface MessageRepository
                extends JpaRepository<Message, Long> {

        /*
         * 최초 채팅 조회
         * 최신 메시지부터 역순으로 가져온다.
         */
        List<Message> findByCoupleOrderByIdDesc(
                        Couple couple,
                        Pageable pageable);

        /*
         * 이전 메시지 조회
         * 현재 화면의 가장 오래된 messageId보다
         * 작은 메시지만 가져온다.
         */
        List<Message> findByCoupleAndIdLessThanOrderByIdDesc(
                        Couple couple,
                        Long beforeMessageId,
                        Pageable pageable);

        Optional<Message> findTopByCoupleIdOrderBySentAtDesc(
                        Long coupleId);

        Optional<Message> findFirstByCoupleIdAndSenderIdNotAndReadAtIsNullOrderByIdAsc(
                        Long coupleId,
                        Long senderId);

        long countByCoupleIdAndSenderIdNotAndReadAtIsNull(
                        Long coupleId,
                        Long userId);

        Optional<Message> findByClientMessageId(
                        String clientMessageId);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("""
                        UPDATE Message m
                           SET m.readAt = :readAt
                         WHERE m.couple.id = :coupleId
                           AND m.sender.id <> :readerId
                           AND m.readAt IS NULL
                        """)
        int markAsRead(
                        @Param("coupleId") Long coupleId,

                        @Param("readerId") Long readerId,

                        @Param("readAt") Instant readAt);
}