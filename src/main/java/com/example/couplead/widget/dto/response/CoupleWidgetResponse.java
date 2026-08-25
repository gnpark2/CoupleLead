package com.example.couplead.widget.dto.response;

public record CoupleWidgetResponse(
                Long coupleId,

                Long partnerId,

                Integer daysTogether,

                Long anniversaryId,
                String anniversaryTitle,
                String anniversaryDate,
                Integer anniversaryDDay,

                String partnerNickname,
                String partnerProfileImage,

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

                String lastMessageAt,

                WidgetPersonResponse me,
                WidgetPersonResponse partner) {
}