package epam.task.gymbootdb.handler;

import epam.task.gymbootdb.service.LoggingService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
public class GymAuthExceptionHandler {

    private static final String ERROR_ID = "errorId";
    private static final String TIMESTAMP = "timestamp";
    private static final String MESSAGE = "message";
    private static final String JWT_EXPIRED = "JWT expired";
    private static final String TOKEN_FORMAT_IS_INVALID = "Token format is invalid";
    private static final String INVALID_TOKEN_SIGNATURE = "Invalid token signature";
    private static final String WRONG_USERNAME_OR_PASSWORD = "Wrong username or password";
    private static final String INVALID_JWT = "Invalid JWT";
    private static final String JWT_EXPIRED_EXCEPTION = "JWT expired Exception";
    private static final String JWT_SIGNATURE_EXCEPTION = "JWT signature Exception";
    private static final String MALFORMED_JWT_EXCEPTION = "Malformed JWT Exception";
    private static final String JWT_EXCEPTION = "JWT Exception";
    private static final String AUTHENTICATION_EXCEPTION = "Authentication Exception";

    private final LoggingService loggingService;

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException e) {
        String errorId = UUID.randomUUID().toString();
        loggingService.logErrorHandler(AUTHENTICATION_EXCEPTION, e.getMessage(), errorId);

        return createUnauthResponseEntity(WRONG_USERNAME_OR_PASSWORD, errorId);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException e) {
        String errorId = UUID.randomUUID().toString();
        loggingService.logErrorHandler(JWT_EXPIRED_EXCEPTION, e.getMessage(), errorId);

        return createUnauthResponseEntity(JWT_EXPIRED, errorId);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJwtException(MalformedJwtException e) {
        String errorId = UUID.randomUUID().toString();
        loggingService.logErrorHandler(MALFORMED_JWT_EXCEPTION, e.getMessage(), errorId);

        return createUnauthResponseEntity(TOKEN_FORMAT_IS_INVALID, errorId);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handleSignatureException(SignatureException e) {
        String errorId = UUID.randomUUID().toString();
        loggingService.logErrorHandler(JWT_SIGNATURE_EXCEPTION, e.getMessage(), errorId);

        return createUnauthResponseEntity(INVALID_TOKEN_SIGNATURE, errorId);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(JwtException e) {
        String errorId = UUID.randomUUID().toString();
        loggingService.logErrorHandler(JWT_EXCEPTION, e.getMessage(), errorId);

        return createUnauthResponseEntity(INVALID_JWT, errorId);
    }

    private static ResponseEntity<Map<String, Object>> createUnauthResponseEntity(String message, String errorId) {
        Map<String, Object> body = Map.of(
                TIMESTAMP, LocalDateTime.now(),
                MESSAGE, message,
                ERROR_ID, errorId
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
