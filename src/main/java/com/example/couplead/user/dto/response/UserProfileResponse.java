package com.example.couplead.user.dto.response;

public record UserProfileResponse(
                Long id,
                String email,
                String nickname,
                String profileImage,
                String country,
                String city,
                String timezone,
                Double latitude,
                Double longitude) {
}