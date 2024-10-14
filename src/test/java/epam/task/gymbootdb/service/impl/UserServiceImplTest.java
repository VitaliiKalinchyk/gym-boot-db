package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.Role;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.UserException;
import epam.task.gymbootdb.repository.RoleRepository;
import epam.task.gymbootdb.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserCredentials userCredentials;
    private ChangePasswordRequest request;

    @BeforeEach
    void setup() {
        user = new User();
        userCredentials = new UserCredentials("Joe", "pass");
        request = new ChangePasswordRequest(userCredentials, "new");
    }

     @Test
     void loadUserByUsername() {
         user.setUsername("Joe");
         user.setPassword("pass");
         user.setRoles(new HashSet<>());

         when(userRepository.findByUsername("Joe")).thenReturn(Optional.of(user));

         UserDetails joe = userService.loadUserByUsername("Joe");

         assertEquals(user.getUsername(), joe.getUsername());
         assertEquals(user.getPassword(), joe.getPassword());
     }

     @Test
     void loadUserByUsernameNoSuchUser() {
         UserException e = assertThrows(UserException.class, () -> userService.loadUserByUsername("Joe"));
         assertEquals("User with username Joe was not found", e.getReason());
     }

    @Test
    void changeStatus() {
        user.setActive(false);

        when(userRepository.findByUsername("Joe")).thenReturn(Optional.of(user));

        userService.changeStatus("Joe");

        assertTrue(user.isActive(), "User should be active");
    }

    @Test
    void changeStatusNoSuchUser() {
        UserException e = assertThrows(UserException.class, () -> userService.changeStatus("Joe"));
        assertEquals("User with username Joe was not found", e.getReason());
    }

    @Test
    void changePassword() {
        when(userRepository.findByUsername(userCredentials.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("newEncodedPassword");

        userService.changePassword(request);

        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    void changePasswordNoSuchUser() {
        UserException e = assertThrows(UserException.class, () -> userService.changePassword(request));
        assertEquals("User with username " + userCredentials.getUsername() + " was not found", e.getReason());
    }

    @Test
    void changePasswordIncorrect() {
        when(userRepository.findByUsername(userCredentials.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())).thenReturn(false);

        PasswordException e = assertThrows(PasswordException.class, () -> userService.changePassword(request));

        assertEquals("Wrong password", e.getReason());
    }

    @Test
    void createAdmin() {
        when(userRepository.existsByUsername("Joe.Doe")).thenReturn(Boolean.FALSE);
        when(roleRepository.findByName(anyString())).thenReturn(new Role());

        userService.createAdmin("Joe.Doe", "password");

        verify(userRepository).save(any());
    }

    @Test
    void createAdminAlreadyExists() {
        when(userRepository.existsByUsername("Joe.Doe")).thenReturn(Boolean.TRUE);

        userService.createAdmin("Joe.Doe", "password");

        verify(userRepository, never()).save(any());
    }
}
