package com.example.couplead.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.couplead.auth.dto.request.LoginRequest;
import com.example.couplead.auth.dto.response.LoginResponse;
import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.auth.security.JwtProvider;
import com.example.couplead.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());

        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return new LoginResponse(accessToken, refreshToken);
    }
}
