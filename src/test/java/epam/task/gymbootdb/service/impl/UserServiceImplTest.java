package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.UserException;
import epam.task.gymbootdb.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private static final User user = new User();
    private static final UserCredentials userCredentials = new UserCredentials("Joe", "pass");
    private static final ChangePasswordRequest request = new ChangePasswordRequest(userCredentials, "new");


    @Test
    void testMatchCredentials() {
        when(userRepository.findByUsername(userCredentials.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())).thenReturn(true);

        assertDoesNotThrow(() -> userService.matchCredentials(userCredentials));
    }


    @Test
    void testMatchCredentialsNoUser() {
        UserException e = assertThrows(UserException.class, () -> userService.matchCredentials(userCredentials));
        assertEquals("User with username " + userCredentials.getUsername() + " was not found", e.getMessage());
    }

    @Test
    void testMatchCredentialsIncorrectPassword() {
        when(userRepository.findByUsername(userCredentials.getUsername())).thenReturn(Optional.of(user));

        PasswordException e = assertThrows(PasswordException.class,
                () -> userService.matchCredentials(userCredentials));
        assertEquals("Wrong password", e.getMessage());
    }

    @Test
    void testChangePassword() {
        when(userRepository.findByUsername(userCredentials.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("newEncodedPassword");

        userService.changePassword(request);

        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    void testChangePasswordNoSuchUser() {
        UserException e = assertThrows(UserException.class, () -> userService.changePassword(request));
        assertEquals("User with username " + userCredentials.getUsername() + " was not found", e.getMessage());
    }

    @Test
    void testChangePasswordIncorrect() {
        when(userRepository.findByUsername(userCredentials.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())).thenReturn(false);

        PasswordException e = assertThrows(PasswordException.class, () -> userService.changePassword(request));

        assertEquals("Wrong password", e.getMessage());
    }
}