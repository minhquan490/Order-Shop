package com.bachlinh.order.mail.http;

import java.io.IOException;
import java.net.http.HttpRequest;

@FunctionalInterface
public interface HttpRequestDecorator {
    HttpRequest.Builder decorate(HttpRequest.Builder requestBuilder) throws IOException;
}
