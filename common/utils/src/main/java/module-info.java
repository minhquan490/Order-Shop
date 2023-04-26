module common.utils {
    requires lombok;
    requires jdk.unsupported;
    requires common.exception;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    exports com.bachlinh.order.utils;
}