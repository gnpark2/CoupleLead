@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private int status;
    private String message;
}
