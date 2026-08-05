package com.example.couplead.widget.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.widget.dto.response.CoupleWidgetResponse;
import com.example.couplead.widget.service.WidgetCacheService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/widgets")
public class WidgetController {
    private final WidgetCacheService widgetCacheService;
    private final CoupleMemberRepository coupleMemberRepository;

    @GetMapping("/couple")
    public ApiResponse<CoupleWidgetResponse> getWidget(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long coupleId = coupleMemberRepository.findByUser(userDetails.getUser())
            .orElseThrow().getCouple().getId();

        return ApiResponse.success(widgetCacheService.getCache(coupleId));
    }
    
}
