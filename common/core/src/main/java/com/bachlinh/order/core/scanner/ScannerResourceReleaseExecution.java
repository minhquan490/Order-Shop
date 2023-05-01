package com.bachlinh.order.core.scanner;

import com.bachlinh.order.annotation.ActiveReflection;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@ActiveReflection
@Log4j2
public class ScannerResourceReleaseExecution implements ApplicationListener<ApplicationReadyEvent> {

    @ActiveReflection
    public ScannerResourceReleaseExecution() {
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Release all resource in scanner");
        ApplicationScanner.clean();
    }
}
