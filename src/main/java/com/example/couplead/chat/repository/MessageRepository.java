package com.example.couplead.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.couplead.chat.domain.Message;
import com.example.couplead.couple.domain.Couple;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByCoupleOrderBySentAtAsc(Couple couple);
    List<Message> findTop500ByCoupleOrderBySentAtDesc(Couple couple);
}
