package com.example.couplead.chat.domain;

import com.example.couplead.common.entity.BaseEntity;
import com.example.couplead.couple.domain.Couple;
import com.example.couplead.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_announcements")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatAnnouncement
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 한 커플당 공지 하나
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id", nullable = false, unique = true)
    private Couple couple;

    /*
     * 어떤 메시지를 공지로 지정했는지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    /*
     * 누가 공지를 지정했는지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /*
     * 원본 메시지를 snapshot으로 보관
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public void update(
            Message message,
            User createdBy) {
        this.message = message;
        this.createdBy = createdBy;
        this.content = message.getContent();
    }
}