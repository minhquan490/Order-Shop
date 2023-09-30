package com.bachlinh.order.web.service.monitor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

@Aspect
public class ServiceMonitor {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Pointcut("@within(com.bachlinh.order.core.annotation.ServiceComponent)")
    private void servicePointcut() {
    }

    @Pointcut("execution(public * *(..))")
    private void servicePublicMethod() {
    }

    @Around("servicePointcut() && servicePublicMethod()")
    public Object monitorService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodExecuteName = proceedingJoinPoint.getSignature().toShortString();
        StopWatch watch = new StopWatch();
        log.info("Begin execute service method [{}]", methodExecuteName);
        watch.start();
        Object result = proceedingJoinPoint.proceed();
        watch.stop();
        log.info("Finish process service method [{}] in [{} ms]", methodExecuteName, watch.getTotalTimeMillis());
        return result;
    }
}
