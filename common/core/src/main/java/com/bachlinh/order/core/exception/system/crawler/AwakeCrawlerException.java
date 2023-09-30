package com.bachlinh.order.core.exception.system.crawler;

import com.bachlinh.order.core.exception.ApplicationException;

public class AwakeCrawlerException extends ApplicationException {
    public AwakeCrawlerException(String message) {
        super(message);
    }

    public AwakeCrawlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
