package com.bachlinh.order.entity;

import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.BaseEntity;

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
}
