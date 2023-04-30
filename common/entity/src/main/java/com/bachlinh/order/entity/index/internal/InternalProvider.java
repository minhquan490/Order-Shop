package com.bachlinh.order.entity.index.internal;

import com.bachlinh.order.entity.DefaultEntityFactory;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.index.spi.SearchManagerFactory;

public final class InternalProvider {
    private InternalProvider() {
    }

    public static EntityFactory.EntityFactoryBuilder useDefaultEntityFactoryBuilder() {
        return new DefaultEntityFactory.DefaultEntityFactoryBuilder();
    }

    public static SearchManagerFactory.Builder useDefaultSearchManagerFactoryBuilder() {
        return DefaultSearchManagerFactory.builder();
    }
}
