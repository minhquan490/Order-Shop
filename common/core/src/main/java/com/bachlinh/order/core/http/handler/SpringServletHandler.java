package com.bachlinh.order.core.http.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface SpringServletHandler {
    <T> ResponseEntity<T> handleServletRequest(String path, HttpServletRequest servletRequest, HttpServletResponse servletResponse);
}
