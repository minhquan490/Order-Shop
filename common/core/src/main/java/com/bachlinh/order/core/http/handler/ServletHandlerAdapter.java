package com.bachlinh.order.core.http.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface ServletHandlerAdapter {
    <T> ResponseEntity<T> handle(HttpServletRequest servletRequest, HttpServletResponse servletResponse);
}
