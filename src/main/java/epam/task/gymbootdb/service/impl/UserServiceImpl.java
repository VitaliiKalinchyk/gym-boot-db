package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.UserException;
import epam.task.gymbootdb.repository.RoleRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.UserService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return new GymUserDetails(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(username)));
    }

    @Override
    public void changeStatus(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));
        boolean status = user.isActive();
        user.setActive(!status);

        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        UserCredentials userCredentials = request.getUserCredentials();
        User entity = getAndCheckUser(userCredentials.getUsername(), userCredentials.getPassword());
        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(entity);
    }

    @Override
    public void createAdmin(String adminName, String adminPassword) {
        if (!userRepository.existsByUsername(adminName)) {
            User user = User.builder()
                    .firstName(adminName)
                    .lastName(adminName)
                    .username(adminName)
                    .password(passwordEncoder.encode(adminPassword))
                    .isActive(true)
                    .roles(Set.of(roleRepository.findByName("ROLE_ADMIN")))
                    .build();

            userRepository.save(user);
            log.warn("Created admin");
        }
    }

    private User getAndCheckUser(String username, String password){
        User entity = userRepository.findByUsername(username).orElseThrow(() -> new UserException(username));
        if (!passwordEncoder.matches(password, entity.getPassword())) throw  new PasswordException();

        return entity;
    }
}
