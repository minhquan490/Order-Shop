package com.bachlinh.order.core.function;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ServletCallback {
    void call(HttpServletRequest request, HttpServletResponse response);
}
