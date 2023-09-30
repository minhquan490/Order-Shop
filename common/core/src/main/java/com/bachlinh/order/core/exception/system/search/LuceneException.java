package com.bachlinh.order.core.exception.system.search;

import com.bachlinh.order.core.exception.ApplicationException;

public class LuceneException extends ApplicationException {
    public LuceneException(String message) {
        super(message);
    }

    public LuceneException(String message, Throwable cause) {
        super(message, cause);
    }
}
