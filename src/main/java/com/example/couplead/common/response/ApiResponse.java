@Getter
@Builder
@AllArgsConstructor
public class ApiResponse {
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
}
