// package com.example.couplead.auth.service;

// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.stereotype.Service;

// import com.example.couplead.auth.dto.request.LoginRequest;
// import com.example.couplead.auth.dto.request.ReissueRequest;
// import com.example.couplead.auth.dto.response.LoginResponse;
// import com.example.couplead.auth.dto.response.TokenResponse;
// import com.example.couplead.auth.security.CustomUserDetails;
// import com.example.couplead.auth.security.JwtProvider;
// import com.example.couplead.user.domain.User;
// import com.example.couplead.user.repository.UserRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class AuthServiceImpl implements AuthService {
//     private final AuthenticationManager authenticationManager;
//     private final JwtProvider jwtProvider;
//     private final RefreshTokenService refreshTokenService;
//     private final UserRepository userRepository;

//     @Override
//     public LoginResponse login(LoginRequest request) {
//         Authentication authentication = authenticationManager.authenticate(
//             new UsernamePasswordAuthenticationToken(request.email(), request.password())
//         );

//         CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
//         User user = principal.getUser();
//         String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
//         String refreshToken = jwtProvider.createRefreshToken(user.getId());

//         refreshTokenService.save(user.getId(), refreshToken);

//         return new LoginResponse(accessToken, refreshToken);
//     }

//     @Override
//     public TokenResponse reissue(ReissueRequest request) {
//         if (!jwtProvider.validateToken(request.refreshToken())) {
//             throw new RuntimeException("유효하지 않은 Refresh Token");
//         }

//         Long userId = jwtProvider.extractUserId(request.refreshToken());

//         if (!refreshTokenService.validate(userId, request.refreshToken())) {
//             throw new RuntimeException("저장된 Refresh Token이 아닙니다.");
//         }

//         User user = userRepository.findById(userId).orElseThrow();
//         String newAccess = jwtProvider.createAccessToken(user.getId(), user.getRole());
//         String newRefresh = jwtProvider.createRefreshToken(user.getId());

//         refreshTokenService.save(user.getId(), newRefresh);

//         return new TokenResponse(newAccess, newRefresh);
//     }

//     @Override
//     public void logout(String refreshToken) {
//     if (!jwtProvider.validateToken(refreshToken)) {
//         throw new RuntimeException("유효하지 않은 Refresh Token");
//     }

//     Long userId = jwtProvider.extractUserId(refreshToken);

//     refreshTokenService.delete(userId);
// }
// }

// 아래는 테스트용 코드

package com.example.couplead.auth.service;

import java.util.concurrent.atomic.AtomicLong;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final AtomicLong LOGIN_COUNTER = new AtomicLong();

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        long totalStart = System.nanoTime();

        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
                )
            );

        long afterAuthentication = System.nanoTime();

        CustomUserDetails principal =
            (CustomUserDetails) authentication.getPrincipal();

        User user = principal.getUser();

        long accessTokenStart = System.nanoTime();

        String accessToken =
            jwtProvider.createAccessToken(
                user.getId(),
                user.getRole()
            );

        long afterAccessToken = System.nanoTime();

        String refreshToken =
            jwtProvider.createRefreshToken(user.getId());

        long afterRefreshToken = System.nanoTime();

        refreshTokenService.save(
            user.getId(),
            refreshToken
        );

        long afterRedis = System.nanoTime();

        /*
         * 모든 로그인마다 로그를 남기면 로그 출력 자체가
         * 테스트 결과에 영향을 줄 수 있으므로 20건마다 1번 기록한다.
         */
        long loginCount = LOGIN_COUNTER.incrementAndGet();

        if (loginCount % 20 == 0) {
            log.info(
                "[LOGIN PERFORMANCE] authentication={}ms, "
                    + "accessToken={}ms, refreshToken={}ms, "
                    + "redis={}ms, total={}ms",
                toMillis(
                    afterAuthentication - totalStart
                ),
                toMillis(
                    afterAccessToken - accessTokenStart
                ),
                toMillis(
                    afterRefreshToken - afterAccessToken
                ),
                toMillis(
                    afterRedis - afterRefreshToken
                ),
                toMillis(
                    afterRedis - totalStart
                )
            );
        }

        return new LoginResponse(
            accessToken,
            refreshToken
        );
    }

    private double toMillis(long nanos) {
        return nanos / 1_000_000.0;
    }

    @Override
    public TokenResponse reissue(ReissueRequest request) {
        if (!jwtProvider.validateToken(request.refreshToken())) {
            throw new RuntimeException(
                "유효하지 않은 Refresh Token"
            );
        }

        Long userId =
            jwtProvider.extractUserId(request.refreshToken());

        if (!refreshTokenService.validate(
            userId,
            request.refreshToken()
        )) {
            throw new RuntimeException(
                "저장된 Refresh Token이 아닙니다."
            );
        }

        User user =
            userRepository.findById(userId)
                .orElseThrow();

        String newAccess =
            jwtProvider.createAccessToken(
                user.getId(),
                user.getRole()
            );

        String newRefresh =
            jwtProvider.createRefreshToken(user.getId());

        refreshTokenService.save(
            user.getId(),
            newRefresh
        );

        return new TokenResponse(
            newAccess,
            newRefresh
        );
    }

    @Override
    public void logout(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException(
                "유효하지 않은 Refresh Token"
            );
        }

        Long userId =
            jwtProvider.extractUserId(refreshToken);

        refreshTokenService.delete(userId);
    }
}