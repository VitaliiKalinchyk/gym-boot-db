package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.UserService;

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
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private static final UserCredentials userCredentials = new UserCredentials("Joe", "pass");
    private static final ChangePasswordRequest changePasswordRequest =
            new ChangePasswordRequest(userCredentials, "newPass");

    @Test
    void testLoginSuccess() {
        ResponseEntity<Void> response = userController.login(userCredentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testLoginNoResponse() {
        doThrow(new RuntimeException()).when(userService).matchCredentials(userCredentials);

        assertThrows(Exception.class, () -> userController.login(userCredentials));
    }

    @Test
    void testChangePasswordSuccess() {
        ResponseEntity<Void> response = userController.changePassword(changePasswordRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testChangePasswordNoResponse() {
        doThrow(new RuntimeException()).when(userService).changePassword(changePasswordRequest);

        assertThrows(Exception.class, () -> userController.changePassword(changePasswordRequest));
    }
}