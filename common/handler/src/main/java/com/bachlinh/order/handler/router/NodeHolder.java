package com.bachlinh.order.handler.router;

import org.springframework.lang.Nullable;

public interface NodeHolder {

    Node[] getChildren();

    @Nullable
    Node getParent();

    @Nullable
    Node getChild(String name);
}
