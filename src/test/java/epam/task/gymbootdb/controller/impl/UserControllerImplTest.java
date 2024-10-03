package epam.task.gymbootdb.controller.impl;

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
class UserControllerImplTest {

    @InjectMocks
    private UserControllerImpl userController;

    @Mock
    private UserService userService;

    private static final UserCredentials userCredentials = new UserCredentials("Joe", "pass");
    private static final ChangePasswordRequest changePasswordRequest =
            new ChangePasswordRequest(userCredentials, "newPass");

    @Test
    void testChangePasswordSuccess() {
        ResponseEntity<Void> response = userController.changePassword(changePasswordRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChangePasswordNoResponse() {
        doThrow(new RuntimeException()).when(userService).changePassword(changePasswordRequest);

        assertThrows(Exception.class, () -> userController.changePassword(changePasswordRequest));
    }
}
