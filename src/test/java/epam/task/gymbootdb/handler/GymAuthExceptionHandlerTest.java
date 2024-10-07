package epam.task.gymbootdb.handler;

import epam.task.gymbootdb.service.LoggingService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GymAuthExceptionHandlerTest {

    @InjectMocks
    private GymAuthExceptionHandler handler;

    @Mock
    private LoggingService loggingService;

    public static final String TRANSACTION_ID = "timestamp";
    public static final String MESSAGE = "message";

    @Test
    void testHandleAuthenticationException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleAuthenticationException(new BadCredentialsException("bad credentials"));

        asserResponseEntity(responseEntity, "Wrong username or password");
        verify(loggingService).logErrorHandler(anyString(), anyString(), anyString());
    }

    @Test
    void testHandleExpiredJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleExpiredJwtException(new ExpiredJwtException(null, null, null));

        asserResponseEntity(responseEntity, "JWT expired");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    @Test
    void testHandleMalformedJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleMalformedJwtException(new MalformedJwtException(null));

        asserResponseEntity(responseEntity, "Token format is invalid");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    @Test
    void testHandleSignatureException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleSignatureException(new SignatureException(null));

        asserResponseEntity(responseEntity, "Invalid token signature");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    @Test
    void testHandleJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleJwtException(new JwtException(null));

        asserResponseEntity(responseEntity, "Invalid JWT");
        verify(loggingService).logErrorHandler(anyString(), any(), anyString());
    }

    private static void asserResponseEntity(ResponseEntity<Map<String, Object>> responseEntity, String message) {
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertTrue(body.containsKey(TRANSACTION_ID));
        assertTrue(body.containsKey(MESSAGE));
        assertTrue(body.containsValue(message));
    }
}
