package com.example.couplead.widget.dto.response;

public record CoupleWidgetResponse(
        Long coupleId,
        Integer daysTogether,
        Long anniversaryId,
        String anniversaryTitle,
        String anniversaryDate,
        Integer anniversaryDDay,
        String partnerNickname,
        boolean partnerOnline,
        boolean partnerTyping,
        String partnerLastSeen,
        Long unreadCount,
        String partnerCity,
        String partnerTimezone,
        String partnerLocalTime,
        Double temperature,
        String weatherCondition,
        String weatherIcon,
        String lastMessageAt
) {
}
