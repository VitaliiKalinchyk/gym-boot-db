package epam.task.gymbootdb.scheduler;

import epam.task.gymbootdb.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExpiredTokenCleanupSchedulerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ExpiredTokenCleanupScheduler expiredTokenCleanupScheduler;

    @Test
    void cleanExpiredToken() {
        expiredTokenCleanupScheduler.cleanExpiredTokens();

        verify(jwtService).deleteExpiredTokens();
    }
}
