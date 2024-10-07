package epam.task.gymbootdb.security;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.security.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    public static final String USERNAME = "Joe";
    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("A".repeat(256), 3600000);
        User user = User.builder().username(USERNAME).password("password").build();
        userDetails = new GymUserDetails(user);
    }

    @Test
    void generateTokenShouldReturnToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsernameShouldReturnUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals(USERNAME, username);
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValidShouldReturnFalseForInvalidToken() {
        String invalidToken = "invalidToken";

        assertThrows(JwtException.class, () -> jwtService.isTokenValid(invalidToken, userDetails));
    }
}
