package com.example.couplead.presence.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.couplead.common.response.ApiResponse;
import com.example.couplead.presence.service.PresenceService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/presence")
public class PresenceRestController {
    private final PresenceService presenceService;

    @GetMapping("/{userId}")
    public ApiResponse<Map<String, Object>> getPresence(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();

        result.put("online", presenceService.isOnline(userId));
        result.put("lastSeen", presenceService.getLastSeen(userId));

        return ApiResponse.success(result);
    }
}
