package com.bachlinh.order.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.internal.JsonStringExceptionTranslator;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.core.server.grpc.GrpcHandler;
import com.bachlinh.order.core.server.jetty.H3JettyServerCustomize;
import com.bachlinh.order.core.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.handler.SpringFrontRequestHandler;
import com.bachlinh.order.web.handler.websocket.SocketHandler;
import com.bachlinh.order.web.handler.websocket.WebSocketManager;
import com.bachlinh.order.web.interceptor.RequestMonitor;
import com.bachlinh.order.web.listener.WebApplicationEventListener;
import com.bachlinh.order.web.servlet.WebServlet;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class WebBaseConfiguration implements WebMvcConfigurer, WebSocketConfigurer {
    private final TokenManager tokenManager;
    private final Environment environment;
    private final DependenciesResolver resolver;

    @Autowired
    WebBaseConfiguration(TokenManager tokenManager, DependenciesResolver resolver, @Value("${active.profile}") String profile) {
        this.tokenManager = tokenManager;
        this.environment = Environment.getInstance(profile);
        this.resolver = resolver;
    }

    @Bean
    JettyServerCustomizer jettyServerCustomizer(@Value("${active.profile}") String profile) {
        return new H3JettyServerCustomize(Integer.parseInt(environment.getProperty("server.port")), environment.getProperty("server.address"), profile);
    }

    @Bean
    GrpcHandler servlet(ApplicationContext applicationContext) {
        return new WebServlet(applicationContext);
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(new RequestMonitor(tokenManager));
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(webSocketSessionManager(), resolver), environment.getProperty("shop.url.socket.endpoint"));
    }
}
