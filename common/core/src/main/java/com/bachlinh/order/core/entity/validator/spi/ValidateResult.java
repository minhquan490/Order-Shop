package com.bachlinh.order.core.entity.validator.spi;

import java.util.Collection;

public interface ValidateResult {
    /**
     * Check validation has error.
     *
     * @return true, if it has error.
     */
    boolean hasError();

    /**
     * Return error messages when a result has error
     *
     * @return messages error
     */
    Collection<String> getMessages();

    void addMessageError(String message);

    static ValidateResult getInstance() {
        return new Result();
    }
}
