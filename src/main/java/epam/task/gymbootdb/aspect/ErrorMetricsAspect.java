package epam.task.gymbootdb.aspect;

import io.micrometer.core.instrument.MeterRegistry;

import lombok.RequiredArgsConstructor;

import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ErrorMetricsAspect {

    private final MeterRegistry registry;

    @Before("execution(public * epam.task.gymbootdb.handler.GymExceptionHandler.*(..))")
    public void countError() {
        registry.counter("error.counter").increment();
    }
}
