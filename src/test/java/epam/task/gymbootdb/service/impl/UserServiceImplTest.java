package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.UserException;
import epam.task.gymbootdb.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
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

    private static User user;
    private static UserCredentials userCredentials;
    private static ChangePasswordRequest request;

    @BeforeEach
    void setup() {
        user = new User();
        userCredentials = new UserCredentials("Joe", "pass");
        request = new ChangePasswordRequest(userCredentials, "new");
    }

     @Test
     void testLoadUserByUsername() {
         user.setUsername("Joe");
         user.setPassword("pass");

         when(userRepository.findByUsername("Joe")).thenReturn(Optional.of(user));

         UserDetails joe = userService.loadUserByUsername("Joe");

         assertEquals(user.getUsername(), joe.getUsername());
         assertEquals(user.getPassword(), joe.getPassword());
     }

     @Test
     void testLoadUserByUsernameNoSuchUser() {
         UserException e = assertThrows(UserException.class, () -> userService.loadUserByUsername("Joe"));
         assertEquals("User with username Joe was not found", e.getReason());
     }

    @Test
    void testChangeStatus() {
        user.setActive(false);

        when(userRepository.findByUsername("Joe")).thenReturn(Optional.of(user));

        userService.changeStatus("Joe");

        assertTrue(user.isActive(), "User should be active");
    }

    @Test
    void testChangeStatusNoSuchUser() {
        UserException e = assertThrows(UserException.class, () -> userService.changeStatus("Joe"));
        assertEquals("User with username Joe was not found", e.getReason());
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
        assertEquals("User with username " + userCredentials.getUsername() + " was not found", e.getReason());
    }

    @Test
    void testChangePasswordIncorrect() {
        when(userRepository.findByUsername(userCredentials.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())).thenReturn(false);

        PasswordException e = assertThrows(PasswordException.class, () -> userService.changePassword(request));

        assertEquals("Wrong password", e.getReason());
    }
}
