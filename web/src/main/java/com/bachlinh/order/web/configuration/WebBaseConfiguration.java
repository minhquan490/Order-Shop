package com.bachlinh.order.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.FrameworkServlet;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.internal.JsonStringExceptionTranslator;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.core.server.H3JettyServerCustomize;
import com.bachlinh.order.core.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.web.handler.SpringFrontRequestHandler;
import com.bachlinh.order.web.handler.websocket.WebSocketManager;
import com.bachlinh.order.web.interceptor.RequestMonitor;
import com.bachlinh.order.web.listener.WebApplicationEventListener;
import com.bachlinh.order.web.servlet.WebServlet;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class WebBaseConfiguration {

    @Bean
    JettyServerCustomizer jettyServerCustomizer(@Value("${active.profile}") String profile) {
        Environment environment = Environment.getInstance(profile);
        return new H3JettyServerCustomize(Integer.parseInt(environment.getProperty("server.port")), environment.getProperty("server.address"), profile);
    }

    @Bean(name = "dispatcherServlet")
    FrameworkServlet servlet(WebApplicationContext webApplicationContext) {
        return new WebServlet(webApplicationContext);
    }

    @Bean
    SpringFrontRequestHandler frontRequestHandler(ControllerManager controllerManager, EntityFactory entityFactory, ExceptionTranslator<NativeResponse<String>> exceptionTranslator) {
        return new SpringFrontRequestHandler(controllerManager, entityFactory, exceptionTranslator);
    }

    @Bean
    ControllerManager controllerManager(@Value("${active.profile}") String profile, ApplicationContext applicationContext) {
        return ControllerManager.getInstance(null, profile, ContainerWrapper.wrap(applicationContext));
    }

    @Bean
    ExceptionTranslator<NativeResponse<String>> exceptionTranslator() {
        return new JsonStringExceptionTranslator();
    }

    @Bean
    WebSocketSessionManager webSocketSessionManager() {
        return new WebSocketManager();
    }

    @Bean
    ApplicationListener<ApplicationEvent> applicationEventApplicationListener(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile) {
        return new WebApplicationEventListener(containerResolver, profile);
    }

    @Bean
    ContainerWrapper containerWrapper(ApplicationContext applicationContext) {
        return ContainerWrapper.wrap(applicationContext);
    }

    @Bean
    WebRequestInterceptor interceptor(TokenManager tokenManager) {
        return new RequestMonitor(tokenManager);
    }
}
