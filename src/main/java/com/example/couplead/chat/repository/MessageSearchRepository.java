package com.example.couplead.chat.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.couplead.chat.document.MessageDocument;

public interface MessageSearchRepository
        extends ElasticsearchRepository<MessageDocument, String> {
}