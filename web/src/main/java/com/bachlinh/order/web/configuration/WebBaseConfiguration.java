package com.bachlinh.order.web.configuration;

import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.core.concurrent.ThreadPoolOptionHolder;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.internal.JsonExceptionTranslator;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.server.jetty.H3JettyServerCustomize;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlBuilderFactory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;
import com.bachlinh.order.handler.router.ServletRouter;
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
import com.bachlinh.order.web.common.entity.DefaultEntityFactory;
import com.bachlinh.order.web.common.entity.DefaultEntityMapperFactory;
import com.bachlinh.order.web.common.interceptor.WebInterceptorConfigurer;
import com.bachlinh.order.web.common.listener.WebApplicationEventListener;
import com.bachlinh.order.web.common.servlet.WebServlet;
import com.bachlinh.order.web.handler.websocket.ProxyRequestUpgradeStrategy;
import com.bachlinh.order.web.handler.websocket.SocketHandler;
import com.bachlinh.order.web.handler.websocket.WebSocketManager;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
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

import java.io.IOException;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebBaseConfiguration extends WebInterceptorConfigurer implements WebSocketConfigurer {
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
//
//    @Bean
//    NettyServer nettyServer() throws SSLException, InterruptedException {
//        return new NettyServer(
//                environment.getProperty("server.ssl.certificate"),
//                environment.getProperty("server.ssl.certificate-private-key"),
//                Integer.parseInt(environment.getProperty("server.port")),
//                environment.getProperty("server.address"),
//                resolver
//        ).start();
//    }

    @Bean(name = "dispatcherServlet")
    FrameworkServlet servlet(WebApplicationContext webApplicationContext) {
        return new WebServlet(webApplicationContext);
    }

    @Bean
    ServletRouter servletRouter() {
        return new ServletRouter(resolver);
    }

    @Bean
    JsonExceptionTranslator jsonStringExceptionTranslator() {
        return new JsonExceptionTranslator();
    }

    @Bean
    ControllerManager controllerManager(@Value("${active.profile}") String profile, ApplicationContext applicationContext) {
        return ControllerManager.getInstance(profile, ContainerWrapper.wrap(applicationContext));
    }

    @Bean
    ExceptionTranslator<NativeResponse<byte[]>> exceptionTranslator() {
        return new JsonExceptionTranslator();
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

    @Bean
    EntityFactory entityFactory(ApplicationContext applicationContext, @Value("${active.profile}") String profile) throws IOException {
        return new DefaultEntityFactory.DefaultEntityFactoryBuilder()
                .container(ContainerWrapper.wrap(applicationContext))
                .profile(profile)
                .build();
    }

    @Bean
    JettyServletWebServerFactory jettyServletWebServerFactory(ObjectProvider<JettyServerCustomizer> serverCustomizers, @Value("${server.port}") int port, ThreadPoolOptionHolder threadPoolOptionHolder) {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.getServerCustomizers().addAll(serverCustomizers.orderedStream().toList());
        factory.setThreadPool(new QueuedThreadPool(Integer.MAX_VALUE, 15, 60000, 0, null, null, threadPoolOptionHolder.getThreadOption().getVirtualThreadFactory()));
        return factory;
    }

    @Bean
    SqlBuilder sqlBuilder(DefaultEntityFactory entityFactory) {
        var seed = entityFactory.getAllContexts();
        SqlBuilderFactory sqlBuilderFactory = SqlBuilderFactory.defaultInstance(seed.values());
        return sqlBuilderFactory.getQueryBuilder();
    }

    @Bean
    EntityMapperFactory entityMapperFactory(EntityFactory entityFactory) {
        return new DefaultEntityMapperFactory(entityFactory);
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
