package com.example.couplead.anniversary.service;

import java.util.List;

import com.example.couplead.anniversary.dto.request.CreateAnniversaryRequest;
import com.example.couplead.anniversary.dto.request.UpdateAnniversaryRequest;
import com.example.couplead.anniversary.dto.response.AnniversaryResponse;

public interface AnniversaryService {
    AnniversaryResponse create(
        Long userId,
        CreateAnniversaryRequest request
    );

    List<AnniversaryResponse> getAll(
        Long userId
    );

    void delete(
        Long userId,
        Long anniversaryId
    );

    AnniversaryResponse update(
        Long userId,
        Long anniversaryId,
        UpdateAnniversaryRequest request
    );

}
