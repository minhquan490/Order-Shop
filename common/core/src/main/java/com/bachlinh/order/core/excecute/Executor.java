package com.bachlinh.order.core.excecute;

import com.bachlinh.order.core.enums.ExecuteEvent;

public interface Executor<T> {
    void execute(BootWrapper<?> wrapper);

    ExecuteEvent runOn();

    Class<T> getBootObjectType();
}
