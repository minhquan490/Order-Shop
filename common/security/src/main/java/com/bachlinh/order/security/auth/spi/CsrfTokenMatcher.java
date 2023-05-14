package com.bachlinh.order.security.auth.spi;

public interface CsrfTokenMatcher {
    boolean match(String actualToken, String expectedToken);
}
