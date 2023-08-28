package com.bachlinh.order.handler.router;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface NodeHolder {

    @NonNull
    Node[] getChildren();

    @Nullable
    Node getParent();

    @Nullable
    Node getChild(String name);
}
