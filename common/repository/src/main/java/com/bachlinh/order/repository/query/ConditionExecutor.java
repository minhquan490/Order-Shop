package com.bachlinh.order.repository.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class ConditionExecutor {
    private final CriteriaBuilder builder;
    private final Collection<Condition> conditions;

    <X> Predicate execute(Root<X> root, Condition condition) {
        return switch (condition.getOperator()) {
            case GE -> builder.ge(root.get(condition.getAttribute()), (Number) condition.getValue());
            case GT -> builder.gt(root.get(condition.getAttribute()), (Number) condition.getValue());
            case EQ -> builder.equal(root.get(condition.getAttribute()), condition.getValue());
            case LE -> builder.le(root.get(condition.getAttribute()), (Number) condition.getValue());
            case LT -> builder.lt(root.get(condition.getAttribute()), (Number) condition.getValue());
            case NEQ -> builder.notEqual(root.get(condition.getAttribute()), condition.getValue());
        };
    }

    public <X> Predicate execute(Root<X> root) {
        AtomicReference<Predicate> resultWrapper = new AtomicReference<>();
        conditions.forEach(condition -> resultWrapper.set(execute(root, condition)));
        return resultWrapper.get();
    }
}
