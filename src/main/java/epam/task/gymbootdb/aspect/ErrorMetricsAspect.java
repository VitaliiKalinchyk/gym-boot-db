package epam.task.gymbootdb.aspect;

import io.micrometer.core.instrument.MeterRegistry;

import lombok.RequiredArgsConstructor;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ErrorMetricsAspect {

    private final MeterRegistry registry;

    @AfterThrowing(pointcut = "execution(* epam.task.gymbootdb..*(..))")
    public void countError() {
        registry.counter("error.counter").increment();
    }
}
