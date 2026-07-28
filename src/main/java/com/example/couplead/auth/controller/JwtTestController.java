package com.example.couplead.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.JwtProvider;
import com.example.couplead.user.domain.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class JwtTestController {

    private final JwtProvider jwtProvider;

    @GetMapping("/token")
    public String token() {

        return jwtProvider.createAccessToken(
                1L,
                Role.USER
        );
    }

}