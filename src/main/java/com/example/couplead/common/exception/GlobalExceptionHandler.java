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
