package com.example.couplead.chat.service;

import java.time.Instant;
import java.time.LocalDate;
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
import com.example.couplead.chat.domain.MessageType;
import com.example.couplead.chat.repository.MessageRepository;
import com.example.couplead.chat.repository.MessageSearchRepository;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
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
                        Instant beforeSentAt,
                        Long beforeMessageId,
                        Long senderId,
                        MessageType type,
                        LocalDate fromDate,
                        LocalDate toDate,
                        ZoneId userZone) {

                String trimmed = keyword == null
                                ? ""
                                : keyword.trim();

                String searchField = useNori
                                ? "content.nori"
                                : "content";

                int requestSize = Math.min(
                                Math.max(size, 1),
                                50);

                BoolQuery.Builder boolQuery = new BoolQuery.Builder()
                                .filter(
                                                f -> f.term(
                                                                t -> t
                                                                                .field("coupleId")
                                                                                .value(coupleId)));

                if (!trimmed.isEmpty()) {
                        boolQuery.must(
                                        m -> m.match(
                                                        match -> match
                                                                        .field(searchField)
                                                                        .query(trimmed)));
                }

                if (senderId != null) {
                        boolQuery.filter(
                                        f -> f.term(
                                                        t -> t
                                                                        .field("senderId")
                                                                        .value(senderId)));
                }

                if (type != null) {
                        boolQuery.filter(
                                        f -> f.term(
                                                        t -> t
                                                                        .field("type")
                                                                        .value(type.name())));
                }

                /*
                 * 사용자가 선택한 날짜를
                 * 사용자 timezone 기준으로 해석한 뒤
                 * UTC Instant 범위로 변환한다.
                 */
                Instant fromInstant = null;
                Instant toExclusiveInstant = null;

                if (fromDate != null) {
                        fromInstant = fromDate
                                        .atStartOfDay(userZone)
                                        .toInstant();
                }

                if (toDate != null) {
                        /*
                         * 종료일의 다음 날 00:00 미만으로 검색.
                         *
                         * 예:
                         * toDate = 2026-08-20
                         *
                         * < 2026-08-21 00:00
                         *
                         * 이렇게 하면 8월 20일 하루 전체가 포함된다.
                         */
                        toExclusiveInstant = toDate
                                        .plusDays(1)
                                        .atStartOfDay(userZone)
                                        .toInstant();
                }

                /*
                 * Elasticsearch에는 UTC 기준으로 범위 검색
                 */
                if (fromInstant != null ||
                                toExclusiveInstant != null) {

                        final Instant finalFromInstant = fromInstant;

                        final Instant finalToExclusiveInstant = toExclusiveInstant;

                        boolQuery.filter(
                                        f -> f.range(
                                                        r -> r.date(
                                                                        d -> {
                                                                                d.field("sentAt");

                                                                                if (finalFromInstant != null) {
                                                                                        d.gte(
                                                                                                        finalFromInstant.toString());
                                                                                }

                                                                                if (finalToExclusiveInstant != null) {
                                                                                        d.lt(
                                                                                                        finalToExclusiveInstant
                                                                                                                        .toString());
                                                                                }

                                                                                return d;
                                                                        })));
                }

                NativeQueryBuilder queryBuilder = NativeQuery.builder()
                                .withQuery(
                                                q -> q.bool(
                                                                boolQuery.build()))
                                .withSort(
                                                sort -> sort.field(
                                                                field -> field
                                                                                .field("sentAt")
                                                                                .order(SortOrder.Desc)))
                                .withSort(
                                                sort -> sort.field(
                                                                field -> field
                                                                                .field("messageId")
                                                                                .order(SortOrder.Desc)))
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
                                                        beforeSentAt.toEpochMilli(),

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
                        Instant nextSentAt,
                        Long nextMessageId,
                        boolean hasMore) {
        }
}