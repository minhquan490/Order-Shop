package com.bachlinh.order.core.entity.index.internal;

import com.bachlinh.order.core.entity.DefaultEntityFactory;
import com.bachlinh.order.core.entity.EntityFactory;
import com.bachlinh.order.core.entity.index.spi.SearchManagerFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class InternalProvider {

    public static EntityFactory.EntityFactoryBuilder useDefaultEntityFactoryBuilder() {
        return new DefaultEntityFactory.DefaultEntityFactoryBuilder();
    }

    public static SearchManagerFactory.Builder useDefaultSearchManagerFactoryBuilder() {
        return DefaultSearchManagerFactory.builder();
    }
}
