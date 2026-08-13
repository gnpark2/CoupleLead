package com.example.couplead.user.dto.response;

import com.example.couplead.user.domain.User;

public record UserMeResponse(
    Long id,
    String email,
    String nickname,
    String profileImage,
    String country,
    String city,
    String timezone,
    Double latitude,
    Double longitude
) {
    public static UserMeResponse from(User user) {
        return new UserMeResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getProfileImage(),
            user.getCountry(),
            user.getCity(),
            user.getTimezone(),
            user.getLatitude(),
            user.getLongitude()
        );
    }
}
