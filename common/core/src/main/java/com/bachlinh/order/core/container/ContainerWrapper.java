package com.bachlinh.order.core.container;

public class ContainerWrapper {
    private final Object container;

    private ContainerWrapper(Object container) {
        this.container = container;
    }

    public static ContainerWrapper wrap(Object container) {
        return new ContainerWrapper(container);
    }

    public Object unwrap() {
        return container;
    }
}
