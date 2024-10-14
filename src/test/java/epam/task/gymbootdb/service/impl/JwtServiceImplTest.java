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

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final String USERNAME = "Joe";

    @Mock
    JwtBlacklistRepository jwtBlacklistRepository;

    private JwtServiceImpl jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(jwtBlacklistRepository, "A".repeat(256), 1_000_000);
        User user = User.builder().username(USERNAME).password("password").roles(new HashSet<>()).build();
        userDetails = new GymUserDetails(user);
    }

    @Test
    void extractUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals(USERNAME, username);
    }

    @Test
    void generateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateTokenWithExpiration() {
        String token = jwtService.generateToken(userDetails, 0);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void isTokenValid() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValidShouldReturnFalseForInvalidToken() {
        String invalidToken = "invalidToken";

        assertThrows(JwtException.class, () -> jwtService.isTokenValid(invalidToken, userDetails));
    }

    @Test
    void isTokenValidShouldReturnFalseForInvalidUsername() {
        User user = User.builder().username("wrongUsername").roles(new HashSet<>()).build();
        GymUserDetails gymUserDetails = new GymUserDetails(user);

        String token = jwtService.generateToken(gymUserDetails);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValidExpiredToken() {
        String token = jwtService.generateToken(userDetails, -1);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValidShouldReturnFalseForBlacklistedToken() {
        String token = jwtService.generateToken(userDetails);

        when(jwtBlacklistRepository.existsById(token)).thenReturn(Boolean.TRUE);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void saveToBlacklist() {
        String token = jwtService.generateToken(userDetails);

        jwtService.saveToBlacklist(token);

        verify(jwtBlacklistRepository).save(any());
    }

    @Test
    void deleteExpiredTokens() {
        jwtService.deleteExpiredTokens();

        verify(jwtBlacklistRepository).deleteAllByExpirationTimeBefore(any());
    }
}
