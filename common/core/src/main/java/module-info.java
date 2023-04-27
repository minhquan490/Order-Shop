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
    requires common.environment;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires org.hibernate.orm.core;
    requires org.hibernate.orm.jcache;
    requires spring.context.support;
    requires cache.api;
    requires spring.tx;
    requires spring.data.commons;
    requires spring.security.core;
    requires org.hibernate.validator;
    requires common.analyzer;
    requires org.apache.lucene.queryparser;
    requires org.aspectj.weaver;
    exports com.bachlinh.order.core.http;
    exports com.bachlinh.order.core.http.handler;
    exports com.bachlinh.order.core.http.converter.spi;
    exports com.bachlinh.order.core.http.parser.spi;
    exports com.bachlinh.order.core.http.ssl.spi;
    exports com.bachlinh.order.core.http.translator.spi;
    exports com.bachlinh.order.core.http.template.spi;
    exports com.bachlinh.order.core.enums;
    exports com.bachlinh.order.core.entity;
    exports com.bachlinh.order.core.entity.cache;
    exports com.bachlinh.order.core.entity.context.spi;
    exports com.bachlinh.order.core.entity.index.spi;
    exports com.bachlinh.order.core.entity.model;
    exports com.bachlinh.order.core.entity.setup.spi;
    exports com.bachlinh.order.core.entity.transaction.spi;
    exports com.bachlinh.order.core.entity.trigger;
    exports com.bachlinh.order.core.entity.trigger.spi;
    exports com.bachlinh.order.core.entity.validator;
    exports com.bachlinh.order.core.entity.validator.spi;
    exports com.bachlinh.order.core.repository;
    exports com.bachlinh.order.core.repository.spi;
    exports com.bachlinh.order.core.scanner;
}