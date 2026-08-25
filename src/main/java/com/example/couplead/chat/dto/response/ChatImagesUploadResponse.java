package com.example.couplead.chat.dto.response;

import java.util.List;

public record ChatImagesUploadResponse(
        List<ChatImageUploadResponse> images) {
}