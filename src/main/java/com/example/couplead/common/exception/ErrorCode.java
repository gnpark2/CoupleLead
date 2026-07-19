@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404,"사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(400,"비밀번호가 올바르지 않습니다."),
    DUPLICATE_EMAIL(400,"이미 존재하는 이메일입니다."),
    INVALID_TOKEN(401,"유효하지 않은 토큰입니다."),
    ACCESS_DENIED(403,"접근 권한이 없습니다.");
    private final int status;
    private final String message;
}
