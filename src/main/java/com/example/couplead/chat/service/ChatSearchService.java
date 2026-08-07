package com.example.couplead.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.couplead.chat.document.MessageDocument;
import com.example.couplead.chat.repository.MessageSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatSearchService {
    private final MessageSearchRepository messageSearchRepository;
    
    public List<MessageDocument> search(Long coupleId, String keyword) {
        return messageSearchRepository.findByCoupleIdAndContentContaining(coupleId, keyword);
    }
}
