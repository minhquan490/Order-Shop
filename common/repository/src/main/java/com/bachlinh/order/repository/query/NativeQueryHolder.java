package com.bachlinh.order.repository.query;

import java.util.Collection;

public interface NativeQueryHolder {
    String getNativeQuery();

    Collection<QueryBinding> getQueryBindings();
}
