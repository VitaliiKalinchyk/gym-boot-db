package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.UserException;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoggingService loggingService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));

        return new GymUserDetails(user);
    }

    @Override
    public void changeStatus(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));
        boolean status = user.isActive();
        user.setActive(!status);
        loggingService.logDebugService("changed status to " + !status, username);

        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        UserCredentials userCredentials = request.getUserCredentials();
        User entity = getAndCheckUser(userCredentials.getUsername(), userCredentials.getPassword());
        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(entity);
        loggingService.logDebugService("changed it's password");
    }

    private User getAndCheckUser(String username, String password){
        User entity = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));
        if (!passwordEncoder.matches(password, entity.getPassword())) throw  new PasswordException();

        return entity;
    }
}
