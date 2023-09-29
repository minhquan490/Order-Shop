package com.bachlinh.order.web.configuration;

import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.DtoValidationRule;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.internal.JsonExceptionTranslator;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.server.netty.listener.HttpFrameListenerFactory;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlBuilderFactory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.handler.interceptor.spi.WebInterceptorChain;
import com.bachlinh.order.handler.service.DefaultServiceManager;
import com.bachlinh.order.handler.service.ServiceManager;
import com.bachlinh.order.handler.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.RuleFactory;
import com.bachlinh.order.validate.rule.RuleManager;
import com.bachlinh.order.validate.rule.ValidationRule;
import com.bachlinh.order.web.common.entity.DefaultEntityFactory;
import com.bachlinh.order.web.common.entity.DefaultEntityMapperFactory;
import com.bachlinh.order.web.common.interceptor.WebInterceptorConfigurer;
import com.bachlinh.order.web.common.listener.NettyHttp2FrameListenerFactory;
import com.bachlinh.order.web.common.listener.NettyHttp3FrameListenerFactory;
import com.bachlinh.order.web.common.listener.WebApplicationEventListener;
import com.bachlinh.order.web.common.servlet.ServletRouter;
import com.bachlinh.order.web.handler.websocket.ProxyRequestUpgradeStrategy;
import com.bachlinh.order.web.handler.websocket.SocketHandler;
import com.bachlinh.order.web.handler.websocket.WebSocketManager;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.incubator.codec.http3.Http3Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;

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

    @Bean(name = "http2FrameListener")
    HttpFrameListenerFactory<Http2Frame> http2FrameHttpFrameListenerFactory() {
        return new NettyHttp2FrameListenerFactory(resolver);
    }

    @Bean(name = "http3FrameListener")
    HttpFrameListenerFactory<Http3Frame> http3FrameHttpFrameListenerFactory() {
        return new NettyHttp3FrameListenerFactory(resolver);
    }

    @Bean
    ServletRouter servletRouter() {
        return new ServletRouter(resolver);
    }

    @Bean
    ServiceManager serviceManager() {
        return new DefaultServiceManager(resolver, environment);
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
    <T extends ValidatedDto> RuleManager ruleManager() {
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
    DtoMapper dtoMapper(@Value("${active.profile}") String profile) {
        return DtoMapper.defaultInstance(new ApplicationScanner(), resolver, Environment.getInstance(profile));
    }

    @Bean
    RequestUpgradeStrategy requestUpgradeStrategy(RepositoryManager repositoryManager, TokenManager tokenManager, UnAuthorizationHandler unAuthorizationHandler) {
        return new ProxyRequestUpgradeStrategy(repositoryManager.getRepository(CustomerRepository.class), tokenManager, unAuthorizationHandler);
    }

    @Bean
    public WebInterceptorChain configInterceptorChain(@Autowired(required = false) Environment environment) {
        return super.configInterceptorChain(resolver, environment == null ? this.environment : environment);
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
