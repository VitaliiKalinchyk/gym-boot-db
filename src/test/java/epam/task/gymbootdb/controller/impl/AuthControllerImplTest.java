package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.JwtTokenDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.AuthService;

import epam.task.gymbootdb.service.JwtService;
import epam.task.gymbootdb.service.LoggingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerImplTest {

    @InjectMocks
    private AuthControllerImpl authController;

    @Mock
    AuthService authService;
    @Mock
    LoggingService loggingService;
    @Mock
    JwtService jwtService;

    @Test
    void testLogin() {
        UserCredentials credentials = new UserCredentials("username", "password");

        when(authService.authenticate(any())).thenReturn("token");

        JwtTokenDto login = authController.login(credentials);

        assertEquals(new JwtTokenDto("token"), login);
        verify(loggingService).logDebugController(anyString(), anyString());
    }

    @Test
    void testLogout() {
        String logout = authController.logout("Bearer token");

        assertEquals("Logout successful", logout);
        verify(jwtService).saveToBlacklist("token");
        verify(loggingService).logDebugController(anyString(), nullable(String.class));
    }
}