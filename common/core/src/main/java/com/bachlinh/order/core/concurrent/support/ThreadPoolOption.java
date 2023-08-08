package com.bachlinh.order.core.concurrent.support;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ThreadPoolOption {
    private int schedulerPoolSize = 1;
    private int asyncExecutorCorePoolSize = 1;
    private int httpExecutorCorePoolSize = 1;
    private int serviceExecutorCorePoolSize = 1;
    private int indexExecutorCorePoolSize = 1;
}
