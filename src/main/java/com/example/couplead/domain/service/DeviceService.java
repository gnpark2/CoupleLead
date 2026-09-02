package com.example.couplead.domain.service;

import com.example.couplead.domain.dto.request.DeviceRegisterRequest;

public interface DeviceService {

    void register(
            Long userId,
            DeviceRegisterRequest request);

    void unregister(
            Long userId,
            String fid);
}