package com.example.couplead.chat.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
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

    public SearchResultPage search(
            Long coupleId,
            String keyword,
            boolean useNori,
            int size,
            LocalDateTime beforeSentAt,
            Long beforeMessageId) {
        String trimmed = keyword == null
                ? ""
                : keyword.trim();

        if (trimmed.isEmpty()) {
            return new SearchResultPage(
                    List.of(),
                    null,
                    null,
                    false);
        }

        String searchField = useNori
                ? "content.nori"
                : "content";

        int requestSize = Math.min(
                Math.max(size, 1),
                50);

        NativeQueryBuilder queryBuilder = NativeQuery.builder()
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
                /*
                 * 정렬 1:
                 * 최신 시간 우선
                 */
                .withSort(
                        sort -> sort.field(
                                field -> field
                                        .field("sentAt")
                                        .order(SortOrder.Desc)))
                /*
                 * 정렬 2:
                 * 같은 시간일 경우 messageId가 큰 것 우선
                 */
                .withSort(
                        sort -> sort.field(
                                field -> field
                                        .field("messageId")
                                        .order(SortOrder.Desc)))
                /*
                 * 다음 페이지 존재 여부를 알아보기 위해
                 * 요청 크기보다 1개 더 가져온다.
                 */
                .withMaxResults(
                        requestSize + 1);

        /*
         * 첫 번째 검색이 아니라면
         * 이전 페이지의 마지막 값 이후부터 검색
         */
        if (beforeSentAt != null &&
                beforeMessageId != null) {

            queryBuilder.withSearchAfter(
                    List.of(
                            beforeSentAt
                                    .atZone(
                                            ZoneId.of(
                                                    "Asia/Seoul"))
                                    .toInstant()
                                    .toEpochMilli(),

                            beforeMessageId));
        }

        NativeQuery query = queryBuilder.build();

        SearchHits<MessageDocument> hits = elasticsearchOperations.search(
                query,
                MessageDocument.class);

        List<SearchHit<MessageDocument>> searchHits = hits.getSearchHits();

        boolean hasMore = searchHits.size() > requestSize;

        List<SearchHit<MessageDocument>> pageHits = hasMore
                ? searchHits.subList(
                        0,
                        requestSize)
                : searchHits;

        List<MessageDocument> messages = pageHits.stream()
                .map(
                        SearchHit::getContent)
                .toList();

        if (pageHits.isEmpty()) {
            return new SearchResultPage(
                    List.of(),
                    null,
                    null,
                    false);
        }

        /*
         * 이번 페이지의 마지막 메시지를
         * 다음 페이지 cursor로 사용
         */
        MessageDocument lastMessage = pageHits.get(
                pageHits.size() - 1).getContent();

        return new SearchResultPage(
                messages,
                lastMessage.getSentAt(),
                lastMessage.getMessageId(),
                hasMore);
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
                                .messageId(
                                        message.getId())
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

    /*
     * Elasticsearch 검색 결과 전용 내부 DTO
     */
    public record SearchResultPage(
            List<MessageDocument> messages,
            LocalDateTime nextSentAt,
            Long nextMessageId,
            boolean hasMore) {
    }
}