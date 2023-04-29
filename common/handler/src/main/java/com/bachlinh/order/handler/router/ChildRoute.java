package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.http.handler.RequestHandler;
import com.bachlinh.order.core.http.handler.SpringServletHandler;

public interface ChildRoute extends RequestHandler {
    ChildRoute getParent();

    String getRootPath();

    String getPath();

    SpringServletHandler getServletHandler();
}
