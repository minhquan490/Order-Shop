package com.bachlinh.order.core.http.translator.spi;

import com.bachlinh.order.annotation.Ignore;
import com.bachlinh.order.annotation.RouteExceptionHandler;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.handler.ErrorHandler;
import com.bachlinh.order.core.http.handler.ExceptionHandler;
import com.bachlinh.order.core.http.handler.ThrowableHandler;
import com.bachlinh.order.exception.system.CriticalException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractExceptionTranslator<T> implements ExceptionTranslator<NativeResponse<T>> {
    private final Map<Class<?>, ExceptionHandler> exceptionHandlerMap = new LinkedHashMap<>();
    private final Map<Class<?>, ErrorHandler> errorHandlerMap = new LinkedHashMap<>();

    @SuppressWarnings("rawtypes")
    protected AbstractExceptionTranslator() {
        String advicePackage = this.getClass().getAnnotation(ComponentScan.class).value()[0];
        ClassPathScanningCandidateComponentProvider scanningCandidateComponentProvider = new ClassPathScanningCandidateComponentProvider(false);
        scanningCandidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(RouteExceptionHandler.class));
        scanningCandidateComponentProvider.addExcludeFilter(new AnnotationTypeFilter(Ignore.class));
        Set<BeanDefinition> beanDefinitions = scanningCandidateComponentProvider.findCandidateComponents(advicePackage);
        beanDefinitions.forEach(bean -> {
            try {
                Class<?> beanClass = Class.forName(bean.getBeanClassName());
                ThrowableHandler instance = (ThrowableHandler) beanClass.getConstructor().newInstance();
                if (!instance.isErrorHandler()) {
                    ExceptionHandler exceptionHandler = (ExceptionHandler) instance;
                    for (Class<?> type : exceptionHandler.activeOnTypes()) {
                        exceptionHandlerMap.put(type, exceptionHandler);
                    }
                }
                if (instance.isErrorHandler()) {
                    ErrorHandler errorHandler = (ErrorHandler) instance;
                    for (Class<?> type : errorHandler.activeOnTypes()) {
                        errorHandlerMap.put(type, errorHandler);
                    }
                }
            } catch (Exception e) {
                throw new CriticalException("Can not load throwable handler [" + bean.getBeanClassName() + "]", e);
            }
        });
    }

    protected Map<Class<?>, ErrorHandler> getErrorHandlerMap() {
        return errorHandlerMap;
    }

    protected Map<Class<?>, ExceptionHandler> getExceptionHandlerMap() {
        return exceptionHandlerMap;
    }
}
