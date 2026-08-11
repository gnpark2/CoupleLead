package com.example.couplead.chat.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.couplead.chat.domain.Message;
import com.example.couplead.couple.domain.Couple;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByCoupleOrderBySentAtAsc(Couple couple);
    List<Message> findTop500ByCoupleOrderBySentAtDesc(Couple couple);
    Optional<Message> findTopByCoupleIdOrderBySentAtDesc(Long coupleId);

    long coucountByCoupleIdAndSenderIdNotAndReadAtIsNull(
        Long coupleId,
        Long userId
    );
    
    @Modifying
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
        @Param("readAt") LocalDateTime readAt
    );
}
