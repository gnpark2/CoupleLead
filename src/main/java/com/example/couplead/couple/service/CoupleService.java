package com.example.couplead.couple.service;

import com.example.couplead.couple.dto.request.ConnectRequest;
import com.example.couplead.couple.dto.response.CoupleResponse;
import com.example.couplead.couple.dto.response.InviteCodeResponse;

public interface CoupleService {
    InviteCodeResponse createInviteCode(Long userId);
    void connect(Long userId, ConnectRequest request);
    CoupleResponse getMyCouple(Long userId);
    void disconnect(Long userId);
}
