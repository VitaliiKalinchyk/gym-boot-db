package epam.task.gymbootdb.scheduler;

import epam.task.gymbootdb.service.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiredTokenCleanupScheduler {

    private final JwtService jwtService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanExpiredTokens() {
        jwtService.deleteExpiredTokens();
    }
}
