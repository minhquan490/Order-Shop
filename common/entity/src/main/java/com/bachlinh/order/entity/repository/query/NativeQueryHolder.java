package com.bachlinh.order.entity.repository.query;

import java.util.Collection;

public interface NativeQueryHolder {
    String getNativeQuery();

    Collection<QueryBinding> getQueryBindings();
}
