package com.example.couplead.event.dto;

import java.time.LocalDate;

public record CoupleAnniversaryUpdatedEvent(
    Long coupleId,
    LocalDate anniversary
) {

}
