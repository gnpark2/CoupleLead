package com.example.couplead.chat.domain;

import java.time.Instant;
import java.time.Instant;

import com.example.couplead.common.entity.BaseEntity;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "messages")
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id", nullable = false)
    private Couple couple;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, length = 1000)
    private String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type = MessageType.TEXT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_message_id")
    private Message replyToMessage;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    private Instant deletedAt;

    @Column(nullable = false)
    private Instant sentAt;

    private Instant readAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean edited = false;

    private Instant editedAt;

    public void markAsRead(Instant readAt) {
        this.readAt = readAt;
    }

    @Column(name = "client_message_id", unique = true)
    private String clientMessageId;

    @Column(name = "media_group_id", length = 36)
    private String mediaGroupId;

    public void editContent(
            String content) {
        this.content = content;
        this.edited = true;
        this.editedAt = Instant.now();
    }

    public void deleteForEveryone() {
        this.deleted = true;
        this.deletedAt = Instant.now();
        this.content = "";
    }
}
