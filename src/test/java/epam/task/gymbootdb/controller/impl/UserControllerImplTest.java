package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

    @InjectMocks
    private UserControllerImpl userController;

    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private LoggingService loggingService;

    private static final String USERNAME = "Joe.Doe";
    private static final String PASSWORD = "pass";
    private static final String NEW_PASS = "newPass";

    private static final UserCredentials userCredentials = new UserCredentials(USERNAME, PASSWORD);
    private static final ChangePasswordRequest request = new ChangePasswordRequest(userCredentials, NEW_PASS);

    @BeforeEach
    void setUp() {
        setUpSecurityContext();
    }

    @Test
    void testChangeActiveStatus() {
        ResponseEntity<Void> response = userController.changeActiveStatus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testChangeActiveStatusNoResponse() {
        doThrow(new RuntimeException()).when(userService).changeStatus(USERNAME);

        assertThrows(Exception.class, () -> userController.changeActiveStatus());
    }

    @Test
    void testChangePasswordSuccess() {
        ResponseEntity<Void> response = userController.changePassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testChangePasswordNoResponse() {
        doThrow(new RuntimeException()).when(userService).changePassword(request);

        assertThrows(Exception.class, () -> userController.changePassword(request));
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);
    }
}
