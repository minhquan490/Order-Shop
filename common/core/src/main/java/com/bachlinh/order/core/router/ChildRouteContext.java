package com.bachlinh.order.core.router;

import com.bachlinh.order.core.http.handler.RequestHandler;

public interface ChildRouteContext {
    ChildRoute getChild(String childName);

    RequestHandler getHandler(String name);

    void addChild(String childName, String rootPath, String fullChildPath);
}
