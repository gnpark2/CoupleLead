package com.example.couplead.couple.dto.response;

import java.time.LocalDate;

public record CoupleResponse(
    Long coupleId,
    String partnerNickname,
    LocalDate anniversary
) {
    
}
