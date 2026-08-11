package com.example.couplead.couple.dto.response;

import java.time.LocalDate;

public record CoupleResponse(
    Long coupleId,
    Long partnerId,
    String partnerNickname,
    String partnerProfileImage,
    String partnerCountry,
    String partnerTimezone,
    LocalDate connectedAt,
    Long daysTogether
) {
    
}
