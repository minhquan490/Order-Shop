package com.bachlinh.order.core.entity.trigger;

import com.bachlinh.order.core.entity.EntityFactory;
import com.bachlinh.order.core.entity.model.BaseEntity;
import com.bachlinh.order.core.entity.trigger.spi.EntityTrigger;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.exception.system.TriggerExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Aspect
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
@Log4j2
public class TriggerInterceptor {
    private final EntityFactory entityFactory;

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    private void triggerOn() {
    }

    @Pointcut("execution(* save*(..))")
    private void triggerOnSavePointcut() {
    }

    @Pointcut("execution(* update*(..))")
    private void triggerOnUpdatePointcut() {
    }

    @Pointcut("execution(* edit*(..))")
    private void triggerOnEditPointcut() {
    }

    @Pointcut("execution(* delete*(..))")
    private void triggerOnDeletePointcut() {
    }

    @Around("triggerOn() && (triggerOnSavePointcut() || triggerOnUpdatePointcut() || triggerOnEditPointcut() || triggerOnDeletePointcut())")
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> Object trigger(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodExecutionName = joinPoint.getSignature().getName();
        Class<? extends BaseEntity> baseEntity = null;
        Set<T> entities = new HashSet<>();
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof BaseEntity casted) {
                baseEntity = casted.getClass();
                entities.add((T) casted);
                break;
            }
            Class<T> argClass = (Class<T>) arg.getClass();
            baseEntity = processArgClass(argClass, entities, arg);
        }
        if (baseEntity == null) {
            return joinPoint.proceed();
        }
        Collection<EntityTrigger<T>> triggers = entityFactory.getEntityContext(baseEntity)
                .getTrigger()
                .stream()
                .map(entityTrigger -> (EntityTrigger<T>) entityTrigger)
                .toList();
        if (methodExecutionName.startsWith("save")) {
            return runTrigger(triggers, entities, TriggerExecution.ON_INSERT, joinPoint);
        }
        if (methodExecutionName.startsWith("update")) {
            return runTrigger(triggers, entities, TriggerExecution.ON_UPDATE, joinPoint);
        }
        if (methodExecutionName.startsWith("delete")) {
            return runTrigger(triggers, entities, TriggerExecution.ON_DELETE, joinPoint);
        }
        throw new TriggerExecutionException("No matching method found");
    }

    private <T extends BaseEntity> void executeBeforeAction(Collection<EntityTrigger<T>> triggers, T entity) {
        Collection<EntityTrigger<T>> beforeAction = triggers.stream()
                .filter(trigger -> trigger.getMode() == TriggerMode.BEFORE)
                .toList();
        beforeAction.forEach(trigger -> trigger.execute(entity));
    }

    private <T extends BaseEntity> void executeAfterAction(Collection<EntityTrigger<T>> triggers, T entity) {
        Collection<EntityTrigger<T>> beforeAction = triggers.stream()
                .filter(trigger -> trigger.getMode() == TriggerMode.AFTER)
                .toList();
        beforeAction.forEach(trigger -> trigger.execute(entity));
    }

    private <T extends BaseEntity> Class<T> processArgClass(Class<T> argClass, Set<T> entities, Object arg) {
        Class<T> baseEntity = null;
        if (argClass.getTypeParameters().length > 0) {
            if (arg instanceof Collection<?>) {
                baseEntity = processOnCollection(entities, arg);
            }
            if (arg instanceof Map<?, ?>) {
                baseEntity = processOnMap(entities, arg);
            }
        }
        return baseEntity;
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseEntity> Class<T> processOnCollection(Set<T> entities, Object arg) {
        Class<T> baseEntity = null;
        if (arg instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return null;
            }
            Class<?> clazz = collection.toArray()[0].getClass();
            if (BaseEntity.class.isAssignableFrom(clazz)) {
                baseEntity = (Class<T>) clazz;
                entities.addAll((Collection<? extends T>) collection);
            }
        }
        return baseEntity;
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseEntity> Class<T> processOnMap(Set<T> entities, Object arg) {
        Class<T> baseEntity = null;
        if (arg instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                return null;
            }
            Class<?> keyClass = map.keySet().toArray()[0].getClass();
            Class<?> valueClass = map.values().toArray()[0].getClass();
            if (BaseEntity.class.isAssignableFrom(keyClass)) {
                baseEntity = (Class<T>) keyClass;
                entities.addAll((Collection<T>) map.keySet());
            }
            if (BaseEntity.class.isAssignableFrom(valueClass)) {
                baseEntity = (Class<T>) valueClass;
                entities.addAll((Collection<T>) map.values());
            }
        }
        return baseEntity;
    }

    private <T extends BaseEntity> Collection<EntityTrigger<T>> extractTriggers(Collection<EntityTrigger<T>> triggers, TriggerExecution triggerExecution) {
        return triggers
                .stream()
                .filter(trigger -> Arrays.asList(trigger.getExecuteOn()).contains(triggerExecution))
                .toList();
    }

    private <T extends BaseEntity> Object runTrigger(Collection<EntityTrigger<T>> bases, Collection<T> entities, TriggerExecution execution, ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<EntityTrigger<T>> triggers = extractTriggers(bases, execution);
        entities.forEach(entity -> executeBeforeAction(triggers, entity));
        Object result = joinPoint.proceed();
        entities.forEach(entity -> executeAfterAction(triggers, entity));
        return result;
    }
}
