package com.bachlinh.order.core.excecute;

import com.bachlinh.order.core.enums.ExecuteEvent;

public sealed interface Executor<T> permits AbstractExecutor {
    void execute(BootWrapper<?> wrapper);

    ExecuteEvent runOn();

    Class<T> getBootObjectType();
}
