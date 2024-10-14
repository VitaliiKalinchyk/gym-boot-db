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

    public static final String TIMESTAMP = "timestamp";
    public static final String MESSAGE = "message";

    private final GymAuthExceptionHandler handler = new GymAuthExceptionHandler();

    @Test
    void handleAuthenticationException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleAuthenticationException(new BadCredentialsException("bad credentials"));

        asserResponseEntity(responseEntity, "Wrong username or password");
    }

    @Test
    void handleExpiredJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleExpiredJwtException(new ExpiredJwtException(null, null, null));

        asserResponseEntity(responseEntity, "JWT expired");
    }

    @Test
    void handleMalformedJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleMalformedJwtException(new MalformedJwtException(null));

        asserResponseEntity(responseEntity, "Token format is invalid");
    }

    @Test
    void handleSignatureException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleSignatureException(new SignatureException(null));

        asserResponseEntity(responseEntity, "Invalid token signature");
    }

    @Test
    void handleJwtException() {
        ResponseEntity<Map<String, Object>> responseEntity =
                handler.handleJwtException(new JwtException(null));

        asserResponseEntity(responseEntity, "Invalid JWT");
    }

    private void asserResponseEntity(ResponseEntity<Map<String, Object>> responseEntity, String message) {
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertTrue(body.containsKey(TIMESTAMP));
        assertTrue(body.containsKey(MESSAGE));
        assertTrue(body.containsValue(message));
    }
}
