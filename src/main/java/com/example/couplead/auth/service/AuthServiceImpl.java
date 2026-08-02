package com.example.couplead.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.couplead.auth.dto.request.LoginRequest;
import com.example.couplead.auth.dto.request.ReissueRequest;
import com.example.couplead.auth.dto.response.LoginResponse;
import com.example.couplead.auth.dto.response.TokenResponse;
import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.auth.security.JwtProvider;
import com.example.couplead.user.domain.User;
import com.example.couplead.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.getUser();
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshTokenService.save(user.getId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public TokenResponse reissue(ReissueRequest request) {
        if (!jwtProvider.validateToken(request.refreshToken())) {
            throw new RuntimeException("유효하지 않은 Refresh Token");
        }

        Long userId = jwtProvider.extractUserId(request.refreshToken());

        if (!refreshTokenService.validate(userId, request.refreshToken())) {
            throw new RuntimeException("저장된 Refresh Token이 아닙니다.");
        }

        User user = userRepository.findById(userId).orElseThrow();
        String newAccess = jwtProvider.createAccessToken(user.getId(), user.getRole());
        String newRefresh = jwtProvider.createRefreshToken(user.getId());

        refreshTokenService.save(user.getId(), newRefresh);

        return new TokenResponse(newAccess, newRefresh);
    }

    @Override
    public void logout(String refreshToken) {
    if (!jwtProvider.validateToken(refreshToken)) {
        throw new RuntimeException("유효하지 않은 Refresh Token");
    }

    Long userId = jwtProvider.extractUserId(refreshToken);

    refreshTokenService.delete(userId);
}
}
