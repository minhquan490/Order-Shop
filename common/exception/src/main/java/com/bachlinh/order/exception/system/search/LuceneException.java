package com.bachlinh.order.exception.system.search;

import com.bachlinh.order.exception.ApplicationException;

public class LuceneException extends ApplicationException {
    public LuceneException(String message) {
        super(message);
    }

    public LuceneException(String message, Throwable cause) {
        super(message, cause);
    }
}
