package com.example.couplead.common.exception;

import org.springframework.http.HttpStatus;

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
    COUPLE_NOT_FOUND(404, "커플을 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND( HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),
    MESSAGE_DELETE_FORBIDDEN( HttpStatus.FORBIDDEN, "본인이 보낸 메시지만 삭제할 수 있습니다."),
    MESSAGE_EDIT_FORBIDDEN( HttpStatus.FORBIDDEN, "본인이 보낸 메시지만 수정할 수 있습니다."),
    MESSAGE_EDIT_NOT_ALLOWED( HttpStatus.BAD_REQUEST, "텍스트 메시지만 수정할 수 있습니다."),
    INVALID_MESSAGE_CONTENT( HttpStatus.BAD_REQUEST, "메시지 내용이 비어 있습니다."),
    INVALID_REQUEST( HttpStatus.BAD_REQUEST, "파일이 없거나 너무 큽니다.");

    private final int status;
    private final String message;

    // HttpStatus 객체를 받아 int 상태 코드로 변환해 저장하는 생성자 추가
    ErrorCode(HttpStatus httpStatus, String message) {
        this.status = httpStatus.value(); // .value()로 int 값 추출
        this.message = message;
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
}
