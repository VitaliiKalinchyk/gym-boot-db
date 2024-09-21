package epam.task.gymbootdb.handler;

import epam.task.gymbootdb.exception.NotFoundException;
import epam.task.gymbootdb.exception.PasswordException;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GymExceptionHandler {

    public static final String TRANSACTION_ID = "transactionId";
    public static final String TIMESTAMP = "timestamp";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException e) {
        log.error("NotFoundException: {}. ErrorHandler. TransactionId: {}", e.getMessage(), MDC.get(TRANSACTION_ID));

        return createResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordException(PasswordException e) {
        log.error("PasswordException: {}. ErrorHandler. TransactionId: {}", e.getMessage(), MDC.get(TRANSACTION_ID));

        return createResponseEntity(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> body = new HashMap<>(Map.of(TIMESTAMP, LocalDateTime.now()));
        body.putAll(e.getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                         Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList()))));
        log.error("Validation error: {}. ErrorHandler. TransactionId: {}", e.getMessage(), MDC.get(TRANSACTION_ID));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception e) {
        log.error("Global Exception: {}. ErrorHandler. TransactionId: {}", e.getMessage(), MDC.get(TRANSACTION_ID));

        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private static ResponseEntity<Map<String, Object>> createResponseEntity(HttpStatus status, String message) {
        Map<String, Object> body = Map.of(TIMESTAMP, LocalDateTime.now(), "message", message);
        return ResponseEntity.status(status).body(body);
    }
}