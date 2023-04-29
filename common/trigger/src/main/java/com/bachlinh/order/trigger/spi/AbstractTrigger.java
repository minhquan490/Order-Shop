package com.bachlinh.order.trigger.spi;

import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.model.BaseEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public abstract class AbstractTrigger<T extends BaseEntity> implements EntityTrigger<T> {
    private final ApplicationContext applicationContext;
    private ThreadPoolTaskExecutor executor;
    private boolean runSync = false;

    protected AbstractTrigger(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected ThreadPoolTaskExecutor getExecutor() {
        if (executor == null) {
            this.executor = getApplicationContext().getBean(ThreadPoolTaskExecutor.class);
        }
        return executor;
    }

    public abstract void doExecute(BaseEntity entity);

    protected abstract String getTriggerName();

    @Override
    public final void execute(BaseEntity entity) {
        if (!runSync) {
            Runnable runnable = () -> doExecute(entity);
            getExecutor().execute(runnable);
        } else {
            doExecute(entity);
        }
    }

    protected void setRunSync(boolean runSync) {
        this.runSync = runSync;
    }
}
