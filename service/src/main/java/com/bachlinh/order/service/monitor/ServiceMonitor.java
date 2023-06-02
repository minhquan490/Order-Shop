package com.bachlinh.order.service.monitor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

@Aspect
@Slf4j
public class ServiceMonitor {

    @Pointcut("@within(com.bachlinh.order.annotation.ServiceComponent)")
    private void servicePointcut() {
    }

    @Pointcut("execution(public * *(..))")
    private void servicePublicMethod() {
    }

    @Around("servicePointcut() && servicePublicMethod()")
    public Object monitorService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodExecuteName = proceedingJoinPoint.getSignature().toShortString();
        StopWatch watch = new StopWatch();
        log.info("Begin execute service method {}", methodExecuteName);
        watch.start();
        Object result = proceedingJoinPoint.proceed();
        watch.stop();
        log.info("Finish process service method in {} ms", watch.getTotalTimeMillis());
        return result;
    }
}
