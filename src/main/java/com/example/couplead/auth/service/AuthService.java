package com.example.couplead.auth.service;

import com.example.couplead.auth.dto.request.LoginRequest;
import com.example.couplead.auth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
