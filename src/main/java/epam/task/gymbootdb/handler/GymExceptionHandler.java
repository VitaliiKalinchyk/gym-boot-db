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

@RestControllerAdvice
@Slf4j
public class GymExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        Map<String, Object> body = Map.of("timestamp", LocalDateTime.now(), "message", e.getMessage());
        log.error("NotFoundException: {}. ErrorHandler. TransactionId: {}", e.getMessage(), MDC.get("transactionId"));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<Object> handlePasswordException(PasswordException e) {
        Map<String, Object> body = Map.of("timestamp", LocalDateTime.now(), "message", e.getMessage());
        log.error("PasswordException: {}. ErrorHandler. TransactionId: {}", e.getMessage(), MDC.get("transactionId"));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> body = new HashMap<>(Map.of("timestamp", LocalDateTime.now()));
        body.putAll(e.getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                         Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList()))));
        log.error("Validation error: {}. ErrorHandler. TransactionId: {}", e.getMessage(), MDC.get("transactionId"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception e) {
        Map<String, Object> body = Map.of("timestamp", LocalDateTime.now(),
                "message", "Unexpected error occurred");
        log.error("Global Exception: {}. ErrorHandler. TransactionId: {}", e.getMessage(), MDC.get("transactionId"));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}