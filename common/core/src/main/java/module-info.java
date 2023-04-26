module common.core {
    requires lombok;
    requires org.apache.lucene.core;
    requires com.fasterxml.jackson.databind;
    requires com.google.api.services.gmail;
    requires com.google.common;
    requires jakarta.servlet;
    requires jakarta.persistence;
    requires java.net.http;
    requires java.base;
    requires common.annotation;
    requires common.exception;
    requires common.utils;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    exports com.bachlinh.order.core.http;
    exports com.bachlinh.order.core.http.converter.spi;
    exports com.bachlinh.order.core.http.parser.spi;
    exports com.bachlinh.order.core.http.ssl.spi;
    exports com.bachlinh.order.core.http.translator.spi;
    exports com.bachlinh.order.core.http.template.spi;
    exports com.bachlinh.order.core.http.handler;
}