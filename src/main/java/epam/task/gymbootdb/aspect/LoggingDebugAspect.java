package epam.task.gymbootdb.aspect;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingDebugAspect {

    @Pointcut("execution(public * epam.task.gymbootdb.service..*(..))")
    public void servicePackagePointcut() {}

    @Pointcut("execution(public * epam.task.gymbootdb.controller..*(..))")
    public void controllerPackagePointcut() {}

    @Before("servicePackagePointcut() || controllerPackagePointcut()")
    public void before(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        log.debug("Method {}.{} was called", signature.getDeclaringTypeName(), signature.getName());
    }

    @AfterReturning("servicePackagePointcut() || controllerPackagePointcut()")
    public void after(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        log.debug("Method {}.{} successfully executed", signature.getDeclaringTypeName(), signature.getName());
    }
}
