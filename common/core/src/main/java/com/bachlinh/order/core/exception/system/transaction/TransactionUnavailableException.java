package com.bachlinh.order.core.exception.system.transaction;

import com.bachlinh.order.core.exception.ApplicationException;

public class TransactionUnavailableException extends ApplicationException {

    private static final String DEFAULT_MESSAGE = "No transaction available, use EntityManager.getTransaction instead";

    public TransactionUnavailableException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public TransactionUnavailableException() {
        super(DEFAULT_MESSAGE);
    }
}
