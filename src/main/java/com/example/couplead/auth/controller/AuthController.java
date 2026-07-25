package com.example.couplead.auth.controller;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.couplead.auth.dto.request.SignupRequest;
import com.example.couplead.auth.dto.response.SignupResponse;
import com.example.couplead.user.service.UserService;
import com.example.couplead.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity.ok(
            ApiResponse.success(response)
        );
    }
}
