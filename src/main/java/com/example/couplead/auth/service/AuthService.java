package com.example.couplead.auth.service;

import com.example.couplead.auth.dto.request.LoginRequest;
import com.example.couplead.auth.dto.request.ReissueRequest;
import com.example.couplead.auth.dto.response.LoginResponse;
import com.example.couplead.auth.dto.response.TokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String refreshToken);
    TokenResponse reissue(ReissueRequest request);
}
