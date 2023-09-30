package com.bachlinh.order.web.common.entity;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.utils.UnsafeUtils;

class FormulaInitializer implements Initializer<FormulaProcessor> {
    @Override
    public FormulaProcessor getObject(Class<?> type, Object... params) {
        try {
            return (FormulaProcessor) UnsafeUtils.allocateInstance(type);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
    }
}
