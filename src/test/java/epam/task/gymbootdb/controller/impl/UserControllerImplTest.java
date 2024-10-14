package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.UserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

    private static final String USERNAME = "Joe.Doe";
    private static final String PASSWORD = "pass";
    private static final String NEW_PASS = "newPass";

    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserControllerImpl userController;

    @BeforeEach
    void setUp() {
        setUpSecurityContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void changeUserActiveStatus() {
        assertDoesNotThrow(() -> userController.changeUserActiveStatus());
        verify(userService).changeStatus(USERNAME);
    }

    @Test
    void changeUserPasswordSuccess() {
        UserCredentials userCredentials = new UserCredentials(USERNAME, PASSWORD);
        ChangePasswordRequest request = new ChangePasswordRequest(userCredentials, NEW_PASS);

        assertDoesNotThrow(() -> userController.changeUserPassword(request));
        verify(userService).changePassword(request);
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USERNAME);
    }
}
