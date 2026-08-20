package com.example.couplead.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ChatSearchPageResponse(
        List<ChatSearchResponse> messages,
        LocalDateTime nextSentAt,
        Long nextMessageId,
        boolean hasMore) {
}