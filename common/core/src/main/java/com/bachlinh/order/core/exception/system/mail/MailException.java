package com.bachlinh.order.core.exception.system.mail;

import com.bachlinh.order.core.exception.ApplicationException;

public class MailException extends ApplicationException {

    public MailException(String message) {
        super(message);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
