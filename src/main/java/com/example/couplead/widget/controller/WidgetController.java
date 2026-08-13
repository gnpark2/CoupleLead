package com.example.couplead.widget.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.auth.security.CustomUserDetails;
import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.couple.domain.CoupleMember;
import com.example.couplead.couple.repository.CoupleMemberRepository;
import com.example.couplead.widget.dto.request.SelectWidgetAnniversaryRequest;
import com.example.couplead.widget.dto.response.CoupleWidgetResponse;
import com.example.couplead.widget.service.WidgetCacheService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/widgets")
public class WidgetController {
    private final WidgetCacheService widgetCacheService;
    private final CoupleMemberRepository coupleMemberRepository;

    @GetMapping("/couple")
    public ApiResponse<CoupleWidgetResponse> getWidget(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long myUserId = userDetails.getUser().getId();

        CoupleMember member = coupleMemberRepository
                .findByUser(userDetails.getUser()).orElseThrow();

        Long coupleId = member.getCouple().getId();
        
        return ApiResponse.success(
                widgetCacheService.getCache(
                        coupleId,
                        myUserId
                )
        );
    }

    @PatchMapping("/anniversary")
    public ApiResponse<Void> selectAnniversary(

            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Valid @RequestBody SelectWidgetAnniversaryRequest request) {

        widgetCacheService.selectAnniversary(
                userDetails.getUser().getId(),
                request.anniversaryId());

        return ApiResponse.success();
    }
}
