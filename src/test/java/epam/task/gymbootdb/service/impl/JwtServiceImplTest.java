package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.JwtBlacklistRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @Mock
    JwtBlacklistRepository jwtBlacklistRepository;

    private UserDetails userDetails;

    private static final String USERNAME = "Joe";

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(jwtBlacklistRepository, "A".repeat(256), 1_000_000);
        User user = User.builder().username(USERNAME).password("password").build();
        userDetails = new GymUserDetails(user);
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals(USERNAME, username);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testIsTokenValid() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValidShouldReturnFalseForInvalidToken() {
        String invalidToken = "invalidToken";

        assertThrows(JwtException.class, () -> jwtService.isTokenValid(invalidToken, userDetails));
    }

    @Test
    void testIsTokenValidShouldReturnFalseForInvalidUsername() {
        User user = User.builder().username("wrongUsername").build();
        GymUserDetails gymUserDetails = new GymUserDetails(user);

        String token = jwtService.generateToken(gymUserDetails);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValidExpiredToken() {
        jwtService = new JwtServiceImpl(jwtBlacklistRepository, "A".repeat(256), 0);
        String token = jwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValidShouldReturnFalseForBlacklistedToken() {
        String token = jwtService.generateToken(userDetails);

        when(jwtBlacklistRepository.existsById(token)).thenReturn(Boolean.TRUE);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testSaveToBlacklist() {
        String token = jwtService.generateToken(userDetails);

        jwtService.saveToBlacklist(token);

        verify(jwtBlacklistRepository).save(any());
    }

    @Test
    void testDeleteExpiredTokens() {
        jwtService.deleteExpiredTokens();

        verify(jwtBlacklistRepository).deleteAllByExpirationTimeBefore(any());
    }
}
