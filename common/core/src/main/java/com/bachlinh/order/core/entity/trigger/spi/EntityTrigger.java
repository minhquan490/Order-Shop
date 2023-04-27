package com.bachlinh.order.core.entity.trigger.spi;

import com.bachlinh.order.core.entity.model.BaseEntity;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;

public sealed interface EntityTrigger<T extends BaseEntity> permits AbstractTrigger {
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
}
