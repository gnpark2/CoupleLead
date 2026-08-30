package com.example.couplead.media.service;

import com.example.couplead.media.dto.request.MediaCallActionRequest;
import com.example.couplead.media.dto.response.MediaInviteResponse;
import com.example.couplead.media.dto.response.MediaTokenResponse;

public interface MediaService {

    MediaTokenResponse createToken(
            Long userId);

    MediaInviteResponse invite(
        Long userId
    );

    void accept(
        Long userId,
        MediaCallActionRequest request
    );

    void reject(
        Long userId,
        MediaCallActionRequest request
    );

    void leave(Long userId);
}