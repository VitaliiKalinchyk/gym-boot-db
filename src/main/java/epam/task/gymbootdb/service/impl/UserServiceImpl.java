package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.UserException;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    public static final String TRANSACTION_ID = "transactionId";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));
        log.debug("User (id = {}) is trying to log in. Service layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return new GymUserDetails(user);
    }

    @Override
    public void changeStatus(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));
        boolean status = user.isActive();
        user.setActive(!status);
        log.debug("User (username = {}) changed status to {}. Service layer. TransactionId: {}",
                username, !status, MDC.get(TRANSACTION_ID));

        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        UserCredentials userCredentials = request.getUserCredentials();
        User entity = getAndCheckUser(userCredentials.getUsername(), userCredentials.getPassword());

        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(entity);
        log.debug("User (id = {}) changed its password. Service layer. TransactionId: {}",
                userCredentials.getUsername(), MDC.get(TRANSACTION_ID));
    }

    private User getAndCheckUser(String username, String password){
        User entity = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));
        if (!passwordEncoder.matches(password, entity.getPassword())) throw  new PasswordException();

        return entity;
    }
}
