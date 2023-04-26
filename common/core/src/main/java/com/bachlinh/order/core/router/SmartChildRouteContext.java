package com.bachlinh.order.core.router;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface SmartChildRouteContext extends ChildRouteContext {
    String parsePathToChildRouteName(String path, String prefix);

    void cachePath(String path, String childName);

    void evictCache(String path);

    <T, U> ResponseEntity<T> handleRequest(U request, String path, String prefix, HttpServletRequest servletRequest, HttpServletResponse servletResponse);
}
