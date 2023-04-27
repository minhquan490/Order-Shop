module aot {
    requires common.annotation;
    requires common.core;
    requires spring.core;
    requires common.utils;
    requires spring.context;
    requires jdk.unsupported;
    exports com.bachlinh.order.aot;
    exports com.bachlinh.order.aot.metadata;
}