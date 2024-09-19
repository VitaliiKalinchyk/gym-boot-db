package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.UserException;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void matchCredentials(UserCredentials user) {
        getAndCheckUser(user.getUsername(), user.getPassword());
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        UserCredentials userCredentials = request.getUserCredentials();
        User entity = getAndCheckUser(userCredentials.getUsername(), userCredentials.getPassword());

        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(entity);
    }

    private User getAndCheckUser(String username, String password){
        User entity = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));
        if (!passwordEncoder.matches(password, entity.getPassword())) throw  new PasswordException();

        return entity;
    }
}