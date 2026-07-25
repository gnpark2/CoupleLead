package com.example.couplead.user.service;

import com.example.couplead.auth.dto.request.SignupRequest;
import com.example.couplead.auth.dto.response.SignupResponse;

public interface UserService {
    SignupResponse signup(SignupRequest request);
}
