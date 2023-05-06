package com.bachlinh.order.exception.system.mail;

import com.bachlinh.order.exception.ApplicationException;

public class MailException extends ApplicationException {

    public MailException(String message) {
        super(message);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
