package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.exception.LoginAttemptException;
import epam.task.gymbootdb.utils.impl.LoginAttemptUtilImpl;
import epam.task.gymbootdb.service.AuthService;

import epam.task.gymbootdb.service.LoggingService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtServiceImpl;
    private final LoginAttemptUtilImpl loginAttempt;
    private final LoggingService loggingService;

    @Override
    public String authenticate(UserCredentials credentials) {
        String username = credentials.getUsername();

        if (loginAttempt.isBlocked(username)) {
            loggingService.logWarnService("is blocked due to too many failed login attempts", username);

            throw new LoginAttemptException(username);
        }

        String token = jwtServiceImpl.generateToken(getUserDetails(credentials, username));
        loggingService.logDebugService("authenticated successfully and token generated", username);

        return token;
    }

    private UserDetails getUserDetails(UserCredentials credentials, String username) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, credentials.getPassword())
            );
            loginAttempt.loginSucceeded(username);
            loggingService.logDebugService("successfully authenticated", username);

            return (UserDetails) authentication.getPrincipal();
        } catch (AuthenticationException e) {
            loginAttempt.loginFailed(username);

            throw e;
        }
    }
}
