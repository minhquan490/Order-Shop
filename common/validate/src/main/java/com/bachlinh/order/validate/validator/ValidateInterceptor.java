package com.bachlinh.order.validate.validator;

import com.bachlinh.order.annotation.Validated;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.exception.http.ValidationFailureException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The aspect for intercept the method to validate entity before save.
 *
 * @author Hoang Minh Quan
 */
@Aspect
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class ValidateInterceptor<T extends BaseEntity> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final EntityFactory entityFactory;

    @Pointcut("@annotation(com.bachlinh.order.annotation.Validated)")
    private void validatePointcut() {
    }

    @Before(value = "validatePointcut()", argNames = "joinPoint")
    @SuppressWarnings("unchecked")
    public void validate(JoinPoint joinPoint) {
        T entity = findEntity(joinPoint);
        Collection<EntityValidator<T>> validators = new ArrayList<>();
        entityFactory.getEntityContext(entity.getClass()).getValidators().forEach(validator -> validators.add((EntityValidator<T>) validator));
        if (validators.isEmpty()) {
            log.info("Skip validate on entity [{}]", entity.getClass().getName());
            return;
        }
        log.info("BEGIN validate entity [{}]", entity.getClass().getName());
        doValidate(validators, entity);
        log.info("END validate entity [{}]", entity.getClass().getName());
    }

    private void doValidate(Collection<EntityValidator<T>> validators, T entity) {
        Set<String> errors = new HashSet<>();
        validators.forEach(entityValidator -> entityValidatorCallback(entityValidator, errors, entity));
        if (!errors.isEmpty()) {
            throw new ValidationFailureException(errors, String.format("Fail when validate entity [%s]", entity.getClass()));
        }
    }

    private void entityValidatorCallback(EntityValidator<T> entityValidator, Set<String> errors, T entity) {
        ValidateResult result = entityValidator.validate(entity);
        if (result.hasError()) {
            errors.addAll(result.getMessages());
        }
    }

    @SuppressWarnings("unchecked")
    private T findEntity(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method adviceMethod = methodSignature.getMethod();
        Validated validated = adviceMethod.getAnnotation(Validated.class);
        return (T) joinPoint.getArgs()[validated.targetIndex()];
    }
}
