package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.JwtToken;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.AuthService;

import epam.task.gymbootdb.service.LoggingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerImplTest {

    public static final UserCredentials CREDENTIALS = new UserCredentials("username", "password");

    @InjectMocks
    private AuthControllerImpl authController;

    @Mock
    AuthService authService;
    @Mock
    LoggingService loggingService;

    @Test
    void testLogin() {
        when(authService.authenticate(any())).thenReturn("token");

        ResponseEntity<JwtToken> login = authController.login(CREDENTIALS);

        assertNotNull(login);
        assertEquals(HttpStatus.OK, login.getStatusCode());
        assertEquals(new JwtToken("token"), login.getBody());
        verify(loggingService).logDebugController(anyString(), anyString());
    }

    @Test
    void testLoginNoResponse() {
        doThrow(new RuntimeException()).when(authService).authenticate(any());

        assertThrows(Exception.class, () -> authController.login(CREDENTIALS));
    }

    @Test
    void testLogout() {
        ResponseEntity<String> logout = authController.logout();

        assertNotNull(logout);
        assertEquals(HttpStatus.OK, logout.getStatusCode());
        assertEquals("Logout successful", logout.getBody());
        verify(loggingService).logDebugController(anyString());
    }
}