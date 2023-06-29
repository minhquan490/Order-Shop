package com.bachlinh.order.repository.monitor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

@Aspect
public class RepositoryMonitor {
    private final Logger log = LoggerFactory.getLogger(getClass());

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
