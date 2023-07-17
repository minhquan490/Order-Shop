package com.bachlinh.order.validate.rule;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.validate.RuleInstanceFailureException;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.base.ValidatedDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
class DefaultRuleFactory implements RuleFactory {

    @Override
    public <T extends ValidatedDto> ValidationRule<T> createRule(Class<ValidationRule<T>> targetType, DependenciesResolver resolver, Environment environment) {
        if (!ensureInstance(targetType)) {
            throw new RuleInstanceFailureException(String.format("Can not instance rule for type [%s], make it will @ActiveReflection to make instance able", targetType.getName()));
        }
        Constructor<ValidationRule<T>> constructor = getInstanceConstructor(targetType);
        try {
            return constructor.newInstance(environment, resolver);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuleInstanceFailureException(String.format("Can not create rule for type [%s]", targetType.getName()), e);
        }
    }

    private boolean ensureInstance(Class<?> type) {
        return type.isAnnotationPresent(ActiveReflection.class);
    }

    private <T extends ValidatedDto> Constructor<ValidationRule<T>> getInstanceConstructor(Class<ValidationRule<T>> type) {
        var paramTypes = new Class<?>[]{Environment.class, DependenciesResolver.class};
        @SuppressWarnings("unchecked")
        Constructor<ValidationRule<T>>[] constructors = (Constructor<ValidationRule<T>>[]) type.getDeclaredConstructors();
        for (Constructor<ValidationRule<T>> con : constructors) {
            var validateResult = Arrays.deepEquals(con.getParameterTypes(), paramTypes);
            if (validateResult) {
                if (log.isDebugEnabled()) {
                    log.debug("Init rule [{}] complete", type.getName());
                }
                return con;
            }
        }
        throw new RuleInstanceFailureException(String.format("Instance constructor of class [%s] not found", type.getName()));
    }
}
