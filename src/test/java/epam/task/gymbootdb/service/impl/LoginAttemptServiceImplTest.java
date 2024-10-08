package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceImplTest {

    private static final int MAX_ATTEMPTS = 3;
    private static final String USERNAME = "Joe.Doe";
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(1);

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptServiceImpl(MAX_ATTEMPTS, LOCKOUT_DURATION);
    }

    @Test
    void testIsBlocked() {
        assertFalse(loginAttemptService.isBlocked(USERNAME));
    }

    @Test
    void testLoginFailedShouldLockUserAfterMaxAttempts() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            loginAttemptService.loginFailed(USERNAME);
        }

        assertTrue(loginAttemptService.isBlocked(USERNAME));
    }

    @Test
    void testLoginSucceededShouldClearAttemptsWhenLoginIsSuccessful() {
        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            loginAttemptService.loginFailed(USERNAME);
        }

        loginAttemptService.loginSucceeded(USERNAME);

        assertFalse(loginAttemptService.isBlocked(USERNAME));
    }

    @Test
    void testRemoveExpiredBlocks() {
        loginAttemptService = new LoginAttemptServiceImpl(1, Duration.ZERO);

        loginAttemptService.loginFailed(USERNAME);
        loginAttemptService.removeExpiredBlocks();

        assertFalse(loginAttemptService.isBlocked(USERNAME));
    }
}
