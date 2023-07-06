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
import org.springframework.lang.NonNull;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.internal.JsonStringExceptionTranslator;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.server.jetty.H3JettyServerCustomize;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;
import com.bachlinh.order.handler.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.RuleFactory;
import com.bachlinh.order.validate.rule.RuleManager;
import com.bachlinh.order.validate.rule.ValidationRule;
import com.bachlinh.order.web.common.interceptor.WebInterceptorConfigurer;
import com.bachlinh.order.web.common.listener.WebApplicationEventListener;
import com.bachlinh.order.web.common.servlet.WebServlet;
import com.bachlinh.order.web.handler.SpringFrontRequestHandler;
import com.bachlinh.order.web.handler.websocket.ProxyRequestUpgradeStrategy;
import com.bachlinh.order.web.handler.websocket.SocketHandler;
import com.bachlinh.order.web.handler.websocket.WebSocketManager;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class WebBaseConfiguration extends WebInterceptorConfigurer implements WebSocketConfigurer {
    private DependenciesResolver resolver;
    private Environment environment;

    WebBaseConfiguration() {
        super(new ApplicationScanner());
    }

    @DependenciesInitialize
    public void inject(DependenciesResolver resolver, @Value("${active.profile}") String profile) {
        this.resolver = resolver;
        this.environment = Environment.getInstance(profile);
    }

    @Bean
    JettyServerCustomizer jettyServerCustomizer(@Value("${active.profile}") String profile) {
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
    <T extends ValidatedDto> RuleManager ruleManager(DependenciesResolver resolver) {
        var ruleFactory = RuleFactory.defaultInstance();
        var rules = new ApplicationScanner()
                .findComponents()
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(DtoValidationRule.class))
                .filter(ValidationRule.class::isAssignableFrom)
                .map(clazz -> {
                    @SuppressWarnings("unchecked")
                    Class<ValidationRule<T>> type = (Class<ValidationRule<T>>) clazz;
                    return ruleFactory.createRule(type, resolver, environment);
                })
                .toList();
        return RuleManager.getBase(rules);
    }

    @Bean
    DtoMapper dtoMapper(DependenciesResolver resolver, @Value("${active.profile}") String profile) {
        return DtoMapper.defaultInstance(new ApplicationScanner(), resolver, Environment.getInstance(profile));
    }

    @Bean
    RequestUpgradeStrategy requestUpgradeStrategy(CustomerRepository customerRepository, TokenManager tokenManager, UnAuthorizationHandler unAuthorizationHandler) {
        return new ProxyRequestUpgradeStrategy(customerRepository, tokenManager, unAuthorizationHandler);
    }

    @Override
    @Bean
    public WebInterceptorChain configInterceptorChain(DependenciesResolver resolver, @Autowired(required = false) Environment environment) {
        return super.configInterceptorChain(resolver, environment == null ? this.environment : environment);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(webSocketSessionManager(), resolver), environment.getProperty("shop.url.websocket"))
                .setAllowedOrigins(environment.getProperty("shop.url.client"))
                .setHandshakeHandler(new AbstractHandshakeHandler() {
                    @Override
                    @NonNull
                    public RequestUpgradeStrategy getRequestUpgradeStrategy() {
                        return resolver.resolveDependencies(RequestUpgradeStrategy.class);
                    }
                });
    }
}
