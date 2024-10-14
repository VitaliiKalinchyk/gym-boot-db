package epam.task.gymbootdb.scheduler;

import epam.task.gymbootdb.service.LoginAttemptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExpiredBlocksCleanupSchedulerTest {

    @InjectMocks
    private ExpiredBlocksCleanupScheduler expiredBlocksCleanupScheduler;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Test
    void cleanExpiredBlocks() {
        expiredBlocksCleanupScheduler.cleanExpiredBlocks();

        verify(loginAttemptService).removeExpiredBlocks();
    }


}