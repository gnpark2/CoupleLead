package com.example.couplead.chat.dto.response;

import java.util.List;

public record ChatHistoryPageResponse(
        List<ChatHistoryResponse> messages,
        Long nextCursor,
        boolean hasMore
) {
}