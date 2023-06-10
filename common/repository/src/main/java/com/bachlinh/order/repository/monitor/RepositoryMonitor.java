package com.bachlinh.order.repository.monitor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
public class RepositoryMonitor {

    @Pointcut("@within(com.bachlinh.order.annotation.RepositoryComponent)")
    private void repositoryPointcut() {
    }

    @Pointcut("execution(public * *(..))")
    private void repositoryPublicMethod() {
    }

    @Around("repositoryPointcut() && repositoryPublicMethod()")
    public Object monitorRepository(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodExecuteName = proceedingJoinPoint.getSignature().toShortString();
        StopWatch watch = new StopWatch();
        log.info("Begin execute repository method name: {}", methodExecuteName);
        watch.start();
        Object result = proceedingJoinPoint.proceed();
        watch.stop();
        log.info("Finish process repository method in {} ms", watch.getTotalTimeMillis());
        return result;
    }
}
