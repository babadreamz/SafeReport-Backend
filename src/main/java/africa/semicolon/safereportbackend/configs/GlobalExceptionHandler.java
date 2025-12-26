package africa.semicolon.safereportbackend.configs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (exception.getMessage().contains("Spam detected")) {
            status = HttpStatus.TOO_MANY_REQUESTS;
        } else if (exception.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        }
        errorResponse.put("timestamp", java.time.LocalDateTime.now());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("status", status.value());
        return new ResponseEntity<>(errorResponse, status);
    }
}
