package epam.task.gymbootdb.security;

import epam.task.gymbootdb.security.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    public static final int MAX_ATTEMPTS = 3;
    public static final String USERNAME = "testUser";
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService(MAX_ATTEMPTS, Duration.ofMinutes(1));
    }

    @Test
    void isBlocked_ShouldReturnFalseWhenUserIsNotBlocked() {
        assertFalse(loginAttemptService.isBlocked(USERNAME));
    }

    @Test
    void loginFailed_ShouldLockUserAfterMaxAttempts() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            loginAttemptService.loginFailed(USERNAME);
        }

        assertTrue(loginAttemptService.isBlocked(USERNAME));
    }


    @Test
    void loginSucceededShouldClearAttemptsWhenLoginIsSuccessful() {
        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            loginAttemptService.loginFailed(USERNAME);
        }

        loginAttemptService.loginSucceeded(USERNAME);

        assertFalse(loginAttemptService.isBlocked(USERNAME));
    }
}
