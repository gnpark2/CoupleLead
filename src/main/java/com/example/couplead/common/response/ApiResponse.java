package com.example.couplead.common.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final int code;
    private final String message;
    private final T data;
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message("요청에 성공했습니다.")
                .data(data)
                .build();
    }
    public static ApiResponse<Void> success() {
        return ApiResponse.<Void>builder()
                .success(true)
                .code(200)
                .message("요청에 성공했습니다.")
                .data(null)
                .build();
    }
}
