package com.example.couplead.user.service;

import com.example.couplead.auth.dto.request.SignupRequest;
import com.example.couplead.auth.dto.response.SignupResponse;
import com.example.couplead.user.dto.request.UpdateLocationRequest;

public interface UserService {
    SignupResponse signup(SignupRequest request);
    void updateLocation(Long userId, UpdateLocationRequest request);
}
