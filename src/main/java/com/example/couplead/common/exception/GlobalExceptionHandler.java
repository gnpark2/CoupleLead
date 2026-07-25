package com.example.couplead.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.couplead.common.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handle(CustomException e) {
        ErrorCode error = e.getErrorCode();
        return ResponseEntity
            .status(error.getStatus())
            .body(
                ErrorResponse.builder()
                    .success(false)
                    .status(error.getStatus())
                    .message(error.getMessage())
                    .build()
            );
    }
}
