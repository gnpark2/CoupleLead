package com.example.couplead.chat.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.couplead.chat.document.MessageDocument;

public interface MessageSearchRepository extends ElasticsearchRepository<MessageDocument, String> {
    List<MessageDocument> findByCoupleIdAndContentContaining(Long coupleId, String keyword);
}
