package com.example.couplead.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couplead.chat.domain.ChatAnnouncement;
import com.example.couplead.chat.domain.Message;
import com.example.couplead.couple.domain.Couple;

public interface ChatAnnouncementRepository
        extends JpaRepository<ChatAnnouncement, Long> {

    Optional<ChatAnnouncement> findByCouple(
            Couple couple);

    Optional<ChatAnnouncement> findByMessage(
            Message message);
}