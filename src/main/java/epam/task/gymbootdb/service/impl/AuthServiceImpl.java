package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.exception.LoginAttemptException;
import epam.task.gymbootdb.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtServiceImpl;
    private final LoginAttemptServiceImpl loginAttempt;

    @Override
    public String authenticate(UserCredentials credentials) {
        String username = credentials.getUsername();

        if (loginAttempt.isBlocked(username)) {
            log.warn("User is blocked due to too many failed login attempts");

            throw new LoginAttemptException(username);
        }

        return jwtServiceImpl.generateToken(getUserDetails(credentials, username));
    }

    private UserDetails getUserDetails(UserCredentials credentials, String username) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, credentials.getPassword())
            );
            loginAttempt.loginSucceeded(username);

            return (UserDetails) authentication.getPrincipal();
        } catch (AuthenticationException e) {
            loginAttempt.loginFailed(username);

            throw e;
        }
    }
}
