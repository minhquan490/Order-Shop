package com.bachlinh.order.trigger;

import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.core.container.DependenciesResolver;

public interface EntityTrigger<T extends BaseEntity<?>> {
    /**
     * Execute operation on specific entity.
     *
     * @param entity The entity for trigger execute on.
     */
    void execute(T entity);

    /**
     * Return mode of trigger.
     *
     * @return Mode of trigger.
     * @see TriggerMode TriggerMode for detail.
     */
    TriggerMode getMode();

    TriggerExecution[] getExecuteOn();

    String getTriggerName();

    void setResolver(DependenciesResolver resolver);
}
