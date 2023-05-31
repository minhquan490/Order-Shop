package com.bachlinh.order.web.configuration;

import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.internal.JsonStringExceptionTranslator;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.core.server.grpc.GrpcServer;
import com.bachlinh.order.core.server.grpc.internal.DefaultGrpcServer;
import com.bachlinh.order.core.tcp.context.WebSocketSessionManager;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.security.auth.internal.TokenManagerProvider;
import com.bachlinh.order.security.auth.spi.TemporaryTokenGenerator;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.filter.grpc.AuthenticationFilter;
import com.bachlinh.order.security.filter.grpc.ClientSecretFilter;
import com.bachlinh.order.security.filter.grpc.CsrfFilter;
import com.bachlinh.order.security.filter.grpc.LoggingRequestFilter;
import com.bachlinh.order.security.filter.grpc.PermissionFilter;
import com.bachlinh.order.security.handler.AccessDeniedHandler;
import com.bachlinh.order.security.handler.ClientSecretHandler;
import com.bachlinh.order.security.handler.CsrfCookieHandler;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.handler.SpringFrontRequestHandler;
import com.bachlinh.order.web.handler.websocket.WebSocketManager;
import com.bachlinh.order.web.listener.WebApplicationEventListener;
import com.bachlinh.order.web.servlet.WebServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class WebBaseConfiguration {
    @Bean
    PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(20);
    }

    @Bean
    TokenManager tokenManager(ApplicationContext applicationContext, @Value("${active.profile}") String profile) {
        TokenManagerProvider provider = new TokenManagerProvider(ContainerWrapper.wrap(applicationContext), profile);
        return provider.getTokenManager();
    }

    @Bean
    CsrfCookieHandler csrfCookieHandler(@Value("${server.address}") String address, TemporaryTokenGenerator temporaryTokenGenerator) {
        return new CsrfCookieHandler("/", address, temporaryTokenGenerator);
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
    AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler();
    }

    @Bean
    UnAuthorizationHandler unAuthorizationHandler() {
        return new UnAuthorizationHandler();
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
    GrpcServer grpcServer(@Value("${server.port}") int port,
                          ApplicationContext applicationContext,
                          DependenciesResolver resolver,
                          @Value("${active.profile}") String profile,
                          ThreadPoolTaskExecutor executor) throws IOException {
        Collection<String> excludeUrls = getExcludeUrls(profile);
        GrpcServer.Builder builder = DefaultGrpcServer.builder(port);
        builder.addService(new WebServlet(applicationContext));
        builder.intercept(new LoggingRequestFilter(resolver, profile).setExcludePaths(excludeUrls));
        builder.intercept(new AuthenticationFilter(resolver, profile).setExcludePaths(excludeUrls));
        builder.intercept(new ClientSecretFilter(resolver, profile).setExcludePaths(excludeUrls));
        builder.intercept(new CsrfFilter(resolver, profile).setExcludePaths(excludeUrls));
        builder.intercept(new PermissionFilter(resolver, profile).setExcludePaths(excludeUrls));
        builder.compressorRegistry(CompressorRegistry.getDefaultInstance());
        builder.decompressorRegistry(DecompressorRegistry.getDefaultInstance());
//        builder.useTransportSecurity(environment.getProperty("server.ssl.certificate"), environment.getProperty("server.ssl.certificate-private-key"));
        builder.keepAliveTime(20, TimeUnit.SECONDS);
        builder.keepAliveTimeout(30, TimeUnit.SECONDS);
        builder.executor(executor);
        return builder.build().start();
    }

    @Bean
    ClientSecretHandler clientSecretHandler() {
        return new ClientSecretHandler();
    }

    private List<String> getExcludeUrls(String profile) {
        Environment environment = Environment.getInstance(profile);
        List<String> excludeUrls = new ArrayList<>();
        excludeUrls.add(environment.getProperty("shop.url.pattern.base"));
        excludeUrls.add(environment.getProperty("shop.url.login"));
        excludeUrls.add(environment.getProperty("shop.url.register"));
        excludeUrls.add(environment.getProperty("shop.url.home"));
        excludeUrls.add(environment.getProperty("shop.url.pattern.resource"));
        excludeUrls.add(environment.getProperty("shop.url.customer.reset.sending-mail"));
        excludeUrls.add(environment.getProperty("shop.url.customer.reset.password"));
        return excludeUrls;
    }
}
