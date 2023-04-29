package com.bachlinh.order.entity.index.spi;

import java.util.Collection;

public interface EntitySearcher {
    Collection<String> search(String keyword);
}
