package epam.task.gymbootdb.aspect;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorMetricsAspectTest {

    @Mock
    private MeterRegistry registry;

    @Mock
    private Counter counter;

    @InjectMocks
    private ErrorMetricsAspect errorMetricsAspect;

    @Test
    void countErrors() {
        when(registry.counter("error.counter")).thenReturn(counter);

        errorMetricsAspect.countError();

        verify(counter).increment();
    }
}
