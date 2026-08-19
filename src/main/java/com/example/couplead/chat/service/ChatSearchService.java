package com.example.couplead.chat.service;

import java.util.List;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.couplead.chat.document.MessageDocument;
import com.example.couplead.chat.domain.Message;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.chat.repository.MessageSearchRepository;

import co.elastic.clients.elasticsearch._types.SortOrder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatSearchService {

    private final MessageSearchRepository messageSearchRepository;
    private final MessageRepository messageRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public List<MessageDocument> search(
            Long coupleId,
            String keyword,
            boolean useNori) {
        String trimmed = keyword == null
                ? ""
                : keyword.trim();

        if (trimmed.isEmpty()) {
            return List.of();
        }

        String searchField = useNori
                ? "content.nori"
                : "content";

        NativeQuery query = NativeQuery.builder()
                .withQuery(
                        q -> q.bool(
                                b -> b
                                        .filter(
                                                f -> f.term(
                                                        t -> t
                                                                .field("coupleId")
                                                                .value(coupleId)))
                                        .must(
                                                m -> m.match(
                                                        match -> match
                                                                .field(searchField)
                                                                .query(trimmed)))))
                .withSort(
                        sort -> sort.field(
                                field -> field
                                        .field("sentAt")
                                        .order(SortOrder.Desc)))
                .build();

        SearchHits<MessageDocument> hits = elasticsearchOperations.search(
                query,
                MessageDocument.class);

        return hits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    @Transactional
    public void reindexAllMessages() {

        messageSearchRepository.deleteAll();

        List<Message> messages = messageRepository.findAll();

        List<MessageDocument> documents = messages.stream()
                .filter(
                        message -> !message.isDeleted())
                .map(
                        message -> MessageDocument.builder()
                                .id(
                                        message.getId()
                                                .toString())
                                .coupleId(
                                        message.getCouple()
                                                .getId())
                                .senderId(
                                        message.getSender()
                                                .getId())
                                .senderNickname(
                                        message.getSender()
                                                .getNickname())
                                .type(
                                        message.getType())
                                .content(
                                        message.getContent())
                                .sentAt(
                                        message.getSentAt())
                                .build())
                .toList();

        messageSearchRepository.saveAll(
                documents);
    }
}