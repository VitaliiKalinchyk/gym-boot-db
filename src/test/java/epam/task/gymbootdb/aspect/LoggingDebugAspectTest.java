package epam.task.gymbootdb.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingDebugAspectTest {

    private final LoggingDebugAspect loggingDebugAspect = new LoggingDebugAspect();

    @Mock
    JoinPoint joinPoint;
    @Mock
    Signature signature;

    @Test
    void servicePackagePointcut() {
        assertDoesNotThrow(loggingDebugAspect::servicePackagePointcut);
    }

    @Test
    void controllerPackagePointcut() {
        assertDoesNotThrow(loggingDebugAspect::controllerPackagePointcut);
    }

    @Test
    void before() {
        when(joinPoint.getSignature()).thenReturn(signature);

        assertDoesNotThrow(() -> loggingDebugAspect.before(joinPoint));
    }

    @Test
    void after() {
        when(joinPoint.getSignature()).thenReturn(signature);

        assertDoesNotThrow(() -> loggingDebugAspect.after(joinPoint));
    }
}