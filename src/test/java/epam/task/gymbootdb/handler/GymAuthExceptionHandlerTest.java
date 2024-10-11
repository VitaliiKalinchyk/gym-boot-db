package epam.task.gymbootdb.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GymAuthExceptionHandlerTest {

    private final GymAuthExceptionHandler handler = new GymAuthExceptionHandler();

    public static final String TRANSACTION_ID = "timestamp";
    public static final String MESSAGE = "message";

    @Test
    void testHandleAuthenticationException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleAuthenticationException(new BadCredentialsException("bad credentials"));

        asserResponseEntity(responseEntity, "Wrong username or password");
    }

    @Test
    void testHandleExpiredJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleExpiredJwtException(new ExpiredJwtException(null, null, null));

        asserResponseEntity(responseEntity, "JWT expired");
    }

    @Test
    void testHandleMalformedJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleMalformedJwtException(new MalformedJwtException(null));

        asserResponseEntity(responseEntity, "Token format is invalid");
    }

    @Test
    void testHandleSignatureException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleSignatureException(new SignatureException(null));

        asserResponseEntity(responseEntity, "Invalid token signature");
    }

    @Test
    void testHandleJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleJwtException(new JwtException(null));

        asserResponseEntity(responseEntity, "Invalid JWT");
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
