package com.example.couplead.chat.dto.response;

import java.time.Instant;
import java.util.List;

public record ChatSearchPageResponse(
        List<ChatSearchResponse> messages,
        Instant nextSentAt,
        Long nextMessageId,
        boolean hasMore) {
}