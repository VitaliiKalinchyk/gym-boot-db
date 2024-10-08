package epam.task.gymbootdb.utils.impl;

import epam.task.gymbootdb.utils.LoginAttemptUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class LoginAttemptUtilImpl implements LoginAttemptUtil {
    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lockoutTime = new ConcurrentHashMap<>();

    private final int maxAttempts;
    private final Duration lockoutDuration;

    public LoginAttemptUtilImpl(@Value("${security.login.attempts}") int maxAttempts,
                                @Value("${security.lockout.duration}")Duration lockoutDuration) {
        this.maxAttempts = maxAttempts;
        this.lockoutDuration = lockoutDuration;
    }

    @Override
    public boolean isBlocked(String username) {
        return lockoutTime.containsKey(username) &&
                (System.currentTimeMillis() - lockoutTime.get(username) < lockoutDuration.toMillis());
    }

    @Override
    public void loginSucceeded(String username) {
        attempts.remove(username);
        lockoutTime.remove(username);
    }

    @Override
    public void loginFailed(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
        if (attempts.get(username) >= maxAttempts) {
            lockoutTime.put(username, System.currentTimeMillis());
        }
    }

    @Scheduled(fixedRateString = "${security.lockout.cleaning-rate}")
    private void removeExpiredBlocks() {
        long currentTime = System.currentTimeMillis();
        lockoutTime.entrySet().removeIf(entry -> currentTime - entry.getValue() > lockoutDuration.toMillis());

        log.debug("Expired lockouts removed. Current lockout entries: {}", lockoutTime.size());
    }
}
