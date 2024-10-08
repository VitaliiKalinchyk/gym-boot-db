package epam.task.gymbootdb.scheduler;

import epam.task.gymbootdb.service.LoginAttemptService;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiredBlocksCleanupScheduler {

    private final LoginAttemptService loginAttemptService;

    @Scheduled(fixedRateString = "${security.lockout.cleaning-rate}")
    public void cleanExpiredBlocks() {
        loginAttemptService.removeExpiredBlocks();
    }
}
