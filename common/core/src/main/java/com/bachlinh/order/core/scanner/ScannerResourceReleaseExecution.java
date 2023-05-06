package com.bachlinh.order.core.scanner;

import org.apache.logging.log4j.Logger;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.enums.ExecuteEvent;
import com.bachlinh.order.core.excecute.AbstractExecutor;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

@ActiveReflection
public class ScannerResourceReleaseExecution extends AbstractExecutor<Void> {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(ScannerResourceReleaseExecution.class);

    @ActiveReflection
    public ScannerResourceReleaseExecution(DependenciesContainerResolver containerResolver) {
        super(containerResolver);
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
