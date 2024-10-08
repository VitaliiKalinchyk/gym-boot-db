package epam.task.gymbootdb.utils.impl;

import epam.task.gymbootdb.utils.LoginAttemptUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptUtilImplTest {

    private static final int MAX_ATTEMPTS = 3;
    private static final String USERNAME = "Joe.Doe";
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(1);

    private LoginAttemptUtil loginAttemptUtil;

    @BeforeEach
    void setUp() {
        loginAttemptUtil = new LoginAttemptUtilImpl(MAX_ATTEMPTS, LOCKOUT_DURATION);
    }

    @Test
    void isBlocked_ShouldReturnFalseWhenUserIsNotBlocked() {
        assertFalse(loginAttemptUtil.isBlocked(USERNAME));
    }

    @Test
    void loginFailed_ShouldLockUserAfterMaxAttempts() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            loginAttemptUtil.loginFailed(USERNAME);
        }

        assertTrue(loginAttemptUtil.isBlocked(USERNAME));
    }


    @Test
    void loginSucceededShouldClearAttemptsWhenLoginIsSuccessful() {
        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            loginAttemptUtil.loginFailed(USERNAME);
        }

        loginAttemptUtil.loginSucceeded(USERNAME);

        assertFalse(loginAttemptUtil.isBlocked(USERNAME));
    }
}
