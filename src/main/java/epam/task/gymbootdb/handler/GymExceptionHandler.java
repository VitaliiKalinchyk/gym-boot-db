package epam.task.gymbootdb.handler;

import epam.task.gymbootdb.exception.GymResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GymExceptionHandler {

    public static final String TRANSACTION_ID = "transactionId";
    public static final String ERROR_ID = "errorId";
    public static final String TIMESTAMP = "timestamp";
    public static final String MESSAGE = "message";

    @ExceptionHandler(GymResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleGymResponseStatusException(GymResponseStatusException e) {
        log.error("GymResponseStatusException: {}. ErrorHandler. TransactionId: {}. ErrorId: {}",
                e.getMessage(), e.getTransactionId(), e.getErrorId());

        return createResponseEntity(e.getStatusCode(), e.getReason(), e.getErrorId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        String errorId = UUID.randomUUID().toString();
        log.error("Validation error: {}. ErrorHandler. TransactionId: {}. ErrorId: {}",
                e.getMessage(), MDC.get(TRANSACTION_ID), errorId);

        Map<String, Object> body = new HashMap<>(Map.of(TIMESTAMP, LocalDateTime.now(), ERROR_ID, errorId));
        body.putAll(e.getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                         Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList()))));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception e) {
        String errorId = UUID.randomUUID().toString();
        log.error("Global Exception: {}. ErrorHandler. TransactionId: {}. ErrorId: {}",
                e.getMessage(), MDC.get(TRANSACTION_ID), errorId);

        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", errorId);
    }

    private static ResponseEntity<Map<String, Object>> createResponseEntity(HttpStatusCode status,
                                                                            String message,
                                                                            String errorId) {
        Map<String, Object> body = Map.of(
                TIMESTAMP, LocalDateTime.now(),
                MESSAGE, message,
                ERROR_ID, errorId
        );

        return ResponseEntity.status(status).body(body);
    }
}