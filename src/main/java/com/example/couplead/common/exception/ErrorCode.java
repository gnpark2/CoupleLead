package com.example.couplead.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404,"사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(400,"비밀번호가 올바르지 않습니다."),
    DUPLICATE_EMAIL(400,"이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(400,"이미 사용 중인 닉네임입니다."),
    INVALID_TOKEN(401,"유효하지 않은 토큰입니다."),
    ACCESS_DENIED(403,"접근 권한이 없습니다."),
    INVALID_INVITE_CODE(400, "유효하지 않은 초대 코드입니다."),
    ALREADY_IN_COUPLE(400, "이미 커플로 연결되어 있습니다."),
    CANNOT_CONNECT_SELF(400, "자기 자신과 연결할 수 없습니다."),
    COUPLE_NOT_FOUND(404, "커플을 찾을 수 없습니다.");
    private final int status;
    private final String message;
}
