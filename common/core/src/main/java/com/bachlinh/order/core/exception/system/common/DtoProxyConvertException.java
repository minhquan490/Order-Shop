package com.bachlinh.order.core.exception.system.common;

import com.bachlinh.order.core.exception.ApplicationException;

public class DtoProxyConvertException extends ApplicationException {
    public DtoProxyConvertException(String message) {
        super(message);
    }

    public DtoProxyConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
