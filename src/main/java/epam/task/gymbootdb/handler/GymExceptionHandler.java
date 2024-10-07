package epam.task.gymbootdb.handler;

import epam.task.gymbootdb.exception.GymResponseStatusException;
import epam.task.gymbootdb.service.LoggingService;

import lombok.RequiredArgsConstructor;

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

@RestControllerAdvice
@RequiredArgsConstructor
public class GymExceptionHandler {

    public static final String ERROR_ID = "errorId";
    public static final String TIMESTAMP = "timestamp";
    public static final String MESSAGE = "message";
    public static final String UNEXPECTED_ERROR_OCCURRED = "Unexpected error occurred";
    public static final String VALIDATION_ERROR = "Validation error";
    public static final String GLOBAL_EXCEPTION = "Global Exception";
    public static final String RESPONSE_STATUS_EXCEPTION = "GymResponseStatus Exception";

    private final LoggingService loggingService;

    @ExceptionHandler(GymResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleGymResponseStatusException(GymResponseStatusException e) {
        loggingService.logErrorHandler(RESPONSE_STATUS_EXCEPTION, e.getReason(), e.getErrorId());

        return createResponseEntity(e.getStatusCode(), e.getReason(), e.getErrorId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        String errorId = UUID.randomUUID().toString();
        loggingService.logErrorHandler(VALIDATION_ERROR, e.getMessage(), errorId);

        Map<String, Object> body = new HashMap<>(Map.of(TIMESTAMP, LocalDateTime.now(), ERROR_ID, errorId));
        body.putAll(e.getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                         Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList()))));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception e) {
        String errorId = UUID.randomUUID().toString();
        loggingService.logErrorHandler(GLOBAL_EXCEPTION, e.getMessage(), errorId);

        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR_OCCURRED, errorId);
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
