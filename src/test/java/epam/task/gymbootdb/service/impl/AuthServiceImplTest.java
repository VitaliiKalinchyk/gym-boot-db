package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.exception.LoginAttemptException;
import epam.task.gymbootdb.security.JwtService;
import epam.task.gymbootdb.security.LoginAttemptService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    public static final String USERNAME = "Joe";
    public static final String PASSWORD = "password";
    public static final String MOCKED_TOKEN = "mockedToken";

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private LoginAttemptService loginAttempt;
    @Mock
    private Authentication authentication;

    private UserCredentials credentials;

    @BeforeEach
    public void setUp() {
        credentials = new UserCredentials(USERNAME, PASSWORD);
    }

    @Test
    void testAuthenticateSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(any())).thenReturn(MOCKED_TOKEN);

        String token = authService.authenticate(credentials);

        assertEquals(MOCKED_TOKEN, token);
        verify(loginAttempt).loginSucceeded(any());
    }

    @Test
    void testAuthenticateUserBlocked() {
        when(loginAttempt.isBlocked(USERNAME)).thenReturn(true);

        LoginAttemptException e = assertThrows(LoginAttemptException.class, () -> authService.authenticate(credentials));
        assertEquals(USERNAME + " is locked. Please try again later.", e.getReason());
    }

    @Test
    void testAuthenticateBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Login failed"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(credentials));
        verify(loginAttempt).loginFailed(any());
    }
}