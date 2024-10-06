package epam.task.gymbootdb.handler;

import epam.task.gymbootdb.exception.TraineeException;

import epam.task.gymbootdb.service.LoggingService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GymExceptionHandlerTest {

    @InjectMocks
    private GymExceptionHandler handler;

    @Mock
    private LoggingService loggingService;

    public static final String TRANSACTION_ID = "timestamp";
    public static final String MESSAGE = "message";
    public static final String FIELD_NAME = "fieldName";
    public static final String ERROR_MESSAGE = "Error message";
    public static final String ERROR_ID = "errorId";

    @Test
    void testGymResponseStatusException() {
        TraineeException e = new TraineeException("username");

        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleGymResponseStatusException(e);

        asserResponseEntity(responseEntity, e.getStatusCode(), e.getReason());
        verify(loggingService).logErrorHandler(anyString(), anyString(), anyString());
    }

    @Test
    void testHandleValidationException() {
        FieldError fieldError = new FieldError("objectName", FIELD_NAME, ERROR_MESSAGE);
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        Mockito.when(exception.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Map<String, Object>> responseEntity = handler.handleValidationException(exception);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(body.containsKey(TRANSACTION_ID));
        assertTrue(body.containsKey(FIELD_NAME));
        assertTrue(body.containsKey(ERROR_ID));
        assertTrue(body.containsValue(Collections.singletonList(ERROR_MESSAGE)));
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    @Test
    void testHandleAuthenticationException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleAuthenticationException(new BadCredentialsException("bad credentials"));

        asserResponseEntity(responseEntity, HttpStatus.UNAUTHORIZED, "Wrong username or password");
        verify(loggingService).logErrorHandler(anyString(), anyString(), anyString());
    }

    @Test
    void testHandleExpiredJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleExpiredJwtException(new ExpiredJwtException(null, null, null));

        asserResponseEntity(responseEntity, HttpStatus.UNAUTHORIZED, "JWT expired");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    @Test
    void testHandleMalformedJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleMalformedJwtException(new MalformedJwtException(null));

        asserResponseEntity(responseEntity, HttpStatus.UNAUTHORIZED, "Token format is invalid");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    @Test
    void testHandleSignatureException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleSignatureException(new SignatureException(null));

        asserResponseEntity(responseEntity, HttpStatus.UNAUTHORIZED, "Invalid token signature");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    @Test
    void testHandleJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleJwtException(new JwtException(null));

        asserResponseEntity(responseEntity, HttpStatus.UNAUTHORIZED, "Invalid JWT");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    @Test
    void testHandleGlobalException() {
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleGlobalException(new Exception());

        asserResponseEntity(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    private static void asserResponseEntity(ResponseEntity<Map<String, Object>> responseEntity,
                                            HttpStatusCode status, String message) {
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(status, responseEntity.getStatusCode());
        assertTrue(body.containsKey(TRANSACTION_ID));
        assertTrue(body.containsKey(MESSAGE));
        assertTrue(body.containsValue(message));
    }
}
