package com.bachlinh.order.core.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.enums.ExecuteEvent;
import com.bachlinh.order.core.excecute.AbstractExecutor;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

@ActiveReflection
public class ScannerResourceReleaseExecution extends AbstractExecutor<Void> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ActiveReflection
    public ScannerResourceReleaseExecution(DependenciesContainerResolver containerResolver, String profile) {
        super(containerResolver, profile);
    }

    @Override
    protected void inject() {
        // Do nothing
    }

    @Override
    protected void doExecute(Void bootObject) {
        log.info("Release all resource in scanner");
        ApplicationScanner.clean();
    }

    @Override
    public ExecuteEvent runOn() {
        return ExecuteEvent.ON_READY;
    }

    @Override
    public Class<Void> getBootObjectType() {
        return Void.class;
    }
}
