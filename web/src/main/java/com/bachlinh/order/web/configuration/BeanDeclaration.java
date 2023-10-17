package com.bachlinh.order.web.configuration;

import com.google.api.services.gmail.GmailScopes;
import io.netty.channel.Channel;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.incubator.codec.http3.Http3Frame;
import jakarta.persistence.EntityManagerFactory;

import com.bachlinh.order.batch.boot.JobCenterBooster;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.core.annotation.DtoValidationRule;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.concurrent.option.ThreadPoolOption;
import com.bachlinh.order.core.concurrent.option.ThreadPoolOptionHolder;
import com.bachlinh.order.core.concurrent.support.DefaultThreadPoolManager;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityMapperFactory;
import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.transaction.shaded.JpaTransactionManager;
import com.bachlinh.order.entity.transaction.spi.TransactionManager;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.handler.interceptor.WebInterceptorChain;
import com.bachlinh.order.handler.service.DefaultServiceManager;
import com.bachlinh.order.handler.service.ServiceManager;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.client.AbstractRestClientFactory;
import com.bachlinh.order.http.client.RestClient;
import com.bachlinh.order.http.client.RestClientFactory;
import com.bachlinh.order.http.server.channel.security.FilterChainAdapter;
import com.bachlinh.order.http.server.channel.stomp.NettyConnectionManager;
import com.bachlinh.order.http.server.channel.stomp.NettySocketConnectionHolder;
import com.bachlinh.order.http.server.channel.stomp.StompConnectionManager;
import com.bachlinh.order.http.server.listener.HttpFrameListenerFactory;
import com.bachlinh.order.http.server.listener.StompFrameListenerFactory;
import com.bachlinh.order.http.translator.internal.JsonExceptionTranslator;
import com.bachlinh.order.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.mail.oauth2.CredentialAdapter;
import com.bachlinh.order.mail.service.GmailSendingService;
import com.bachlinh.order.mail.template.EmailTemplateProcessor;
import com.bachlinh.order.repository.RepositoryManager;
import com.bachlinh.order.repository.cache.CacheManager;
import com.bachlinh.order.repository.cache.implementor.EhHeapCacheManager;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlBuilderFactory;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.trigger.EntityTriggerManager;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.bachlinh.order.validate.rule.RuleFactory;
import com.bachlinh.order.validate.rule.RuleManager;
import com.bachlinh.order.validate.rule.ValidationRule;
import com.bachlinh.order.web.common.batch.RepositoryJobManagerBuilder;
import com.bachlinh.order.web.common.entity.DefaultEntityFactory;
import com.bachlinh.order.web.common.entity.DefaultEntityMapperFactory;
import com.bachlinh.order.web.common.listener.Netty2FrameListenerFactory;
import com.bachlinh.order.web.common.listener.Netty3FrameListenerFactory;
import com.bachlinh.order.web.common.listener.NettyWebSocketFrameListenerFactory;
import com.bachlinh.order.web.common.security.AuthenticationFilter;
import com.bachlinh.order.web.common.security.LoggingRequestFilter;
import com.bachlinh.order.web.common.servlet.ServletRouter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.PathMatcher;

@Configuration
public class BeanDeclaration extends SecurityModuleConfigure {

    public BeanDeclaration() {
        super(new ApplicationScanner());
    }

    @Bean
    public DependenciesContainerResolver containerResolver(ApplicationContext applicationContext, @Value("${active.profile}") String profile) {
        return DependenciesContainerResolver.buildResolver(applicationContext, profile);
    }

    @Bean
    public DependenciesResolver resolver(DependenciesContainerResolver containerResolver) {
        return containerResolver.getDependenciesResolver();
    }

    @Bean
    public JobManager jobManager(DependenciesResolver resolver, @Value("${active.profile}") String profile) {
        JobCenterBooster booster = new JobCenterBooster(resolver, profile);
        JobManager.Builder builder = new RepositoryJobManagerBuilder();
        JobManager jobManager = builder.dependenciesResolver(resolver)
                .profile(profile)
                .build(booster);
        jobManager.startJob();
        return jobManager;
    }

    @Bean
    public CacheManager<?> cacheManager() {
        return new EhHeapCacheManager();
    }

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean sessionFactoryBean(ThreadPoolOptionHolder threadPoolOptionHolder) {
        return super.sessionFactoryBean(threadPoolOptionHolder);
    }

    @Override
    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return super.transactionManager(entityManagerFactory);
    }

    @Override
    @Bean
    public EntityFactory entityFactory(ApplicationContext applicationContext, @Value("${active.profile}") String profile) throws IOException {
        return super.entityFactory(applicationContext, profile);
    }

    @Override
    @Bean
    public EntityProxyFactory entityProxyFactory() {
        return super.entityProxyFactory();
    }

    @Override
    @Bean
    public RepositoryManager repositoryManager(DependenciesContainerResolver containerResolver) {
        return super.repositoryManager(containerResolver);
    }

    @Override
    @Bean
    public TransactionManager<?> applicationTransactionManager(DependenciesResolver resolver) {
        return super.applicationTransactionManager(resolver);
    }

    @Override
    @Bean
    public AuditorAware<Object> auditorProvider() {
        return super.auditorProvider();
    }

    @Override
    @Value("${active.profile}")
    public void configProfile(String profile) {
        super.configProfile(profile);
    }

    @Bean
    public GmailSendingService emailSendingService(CredentialAdapter credentialAdapter) throws IOException, URISyntaxException {
        return GmailSendingService.defaultService(credentialAdapter);
    }

    @Bean
    public EmailTemplateProcessor processor() {
        return EmailTemplateProcessor.defaultInstance();
    }

    @Bean
    public CredentialAdapter credentialAdapter(@Value("${active.profile}") String profile) {
        return new CredentialAdapter() {
            @Override
            public InputStream getCredentialResources() throws IOException, URISyntaxException {
                return new URI(getEnvironment().getProperty("google.email.credentials")).toURL().openStream();
            }

            @Override
            public String getSslPemLocation() {
                return getEnvironment().getProperty("server.ssl.certificate");
            }

            @Override
            public String getSslPrivateKeyLocation() {
                return getEnvironment().getProperty("server.ssl.certificate-private-key");
            }

            @Override
            public String[] getGmailScope() {
                return new String[]{GmailScopes.MAIL_GOOGLE_COM};
            }
        };
    }

    @Bean
    public RestClient restTemplate(@Value("${server.ssl.certificate}") String certPath, @Value("${server.ssl.certificate-private-key}") String keyPath) throws Exception {
        RestClientFactory.Builder builder = AbstractRestClientFactory.javaBaseRestClientBuilder();
        return builder.pemCertificatePath(certPath)
                .pemCertificateKeyPath(keyPath)
                .build()
                .create();
    }

    @Override
    @Bean
    public PathMatcher pathMatcher() {
        return super.pathMatcher();
    }

    @Override
    @Bean
    public AuthenticationFilter authenticationFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) {
        return super.authenticationFilter(containerResolver, getEnvironment().getEnvironmentName(), pathMatcher);
    }

    @Override
    @Bean
    public LoggingRequestFilter loggingRequestFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) {
        return super.loggingRequestFilter(containerResolver, profile, pathMatcher);
    }

    @Override
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return super.passwordEncoder();
    }

    @Override
    @Bean
    public TokenManager tokenManager(ApplicationContext applicationContext, @Value("${active.profile}") String profile) {
        return super.tokenManager(applicationContext, profile);
    }

    @Override
    @Bean
    public UnAuthorizationHandler unAuthorizationHandler() {
        return super.unAuthorizationHandler();
    }

    @Override
    @Bean
    @Primary
    public SecurityFilterChain filterChain(HttpSecurity http, DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) throws Exception {
        return super.filterChain(http, containerResolver, profile, pathMatcher);
    }

    @Override
    @Bean
    public FilterChainProxy filterChainProxy(SecurityFilterChain securityFilterChain) {
        return super.filterChainProxy(securityFilterChain);
    }

    @Override
    @Bean
    public FilterChainAdapter filterChainAdapter(DependenciesContainerResolver containerResolver) {
        return super.filterChainAdapter(containerResolver);
    }

    @Bean
    @Primary
    public ThreadPoolManager threadPoolManager() {
        ThreadPoolOption virtualThreadPoolOption = ThreadPoolOption.ofVirtual();
        virtualThreadPoolOption.setSchedulerPoolSize(6);
        virtualThreadPoolOption.setHttpExecutorCorePoolSize(3);
        virtualThreadPoolOption.setIndexExecutorCorePoolSize(50);
        virtualThreadPoolOption.setAsyncExecutorCorePoolSize(10);
        virtualThreadPoolOption.setServiceExecutorCorePoolSize(10);
        return new DefaultThreadPoolManager(virtualThreadPoolOption);
    }

    @Bean
    public ThreadPoolOptionHolder threadPoolOptionHolder(ThreadPoolManager threadPoolManager) {
        return (ThreadPoolOptionHolder) threadPoolManager;
    }

    @Bean(name = "http2FrameListener")
    public HttpFrameListenerFactory<Http2Frame> http2FrameHttpFrameListenerFactory(DependenciesResolver resolver) {
        return new Netty2FrameListenerFactory(resolver);
    }

    @Bean(name = "http3FrameListener")
    public HttpFrameListenerFactory<Http3Frame> http3FrameHttpFrameListenerFactory(DependenciesResolver resolver) {
        return new Netty3FrameListenerFactory(resolver);
    }

    @Bean(name = "stompFrameListener")
    public StompFrameListenerFactory stompFrameHttpFrameListenerFactory(DependenciesResolver resolver) {
        return new NettyWebSocketFrameListenerFactory(resolver);
    }

    @Bean("nettyConnectionManager")
    public StompConnectionManager<Channel> stompConnectionManager() {
        return new NettyConnectionManager(new NettySocketConnectionHolder());
    }

    @Bean(name = "router")
    public ServletRouter servletRouter(DependenciesResolver resolver) {
        return new ServletRouter(resolver);
    }

    @Bean
    public ServiceManager serviceManager(DependenciesResolver resolver) {
        return new DefaultServiceManager(resolver, getEnvironment());
    }

    @Bean
    public JsonExceptionTranslator jsonStringExceptionTranslator() {
        return new JsonExceptionTranslator();
    }

    @Bean
    public ControllerManager controllerManager(@Value("${active.profile}") String profile, ApplicationContext applicationContext) {
        return ControllerManager.getInstance(profile, ContainerWrapper.wrap(applicationContext));
    }

    @Bean
    public ExceptionTranslator<NativeResponse<byte[]>> exceptionTranslator() {
        return new JsonExceptionTranslator();
    }

    @Bean
    public ContainerWrapper containerWrapper(ApplicationContext applicationContext) {
        return ContainerWrapper.wrap(applicationContext);
    }

    @Bean
    public <T extends ValidatedDto> RuleManager ruleManager(DependenciesResolver resolver) {
        var ruleFactory = RuleFactory.defaultInstance();
        var rules = new ApplicationScanner()
                .findComponents()
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(DtoValidationRule.class))
                .filter(ValidationRule.class::isAssignableFrom)
                .map(clazz -> {
                    @SuppressWarnings("unchecked")
                    Class<ValidationRule<T>> type = (Class<ValidationRule<T>>) clazz;
                    return ruleFactory.createRule(type, resolver, getEnvironment());
                })
                .toList();
        return RuleManager.getBase(rules);
    }

    @Bean
    public WebInterceptorChain configInterceptorChain(DependenciesResolver resolver) {
        return super.configInterceptorChain(resolver, getEnvironment());
    }

    @Bean
    public SqlBuilder sqlBuilder(EntityFactory entityFactory) {
        String driverClassName = getEnvironment().getProperty("server.database.driver");
        var seed = ((DefaultEntityFactory) entityFactory).getAllContexts();
        SqlBuilderFactory sqlBuilderFactory = SqlBuilderFactory.defaultInstance(seed.values(), driverClassName);
        return sqlBuilderFactory.getQueryBuilder();
    }

    @Bean
    public EntityMapperFactory entityMapperFactory(EntityFactory entityFactory) {
        return new DefaultEntityMapperFactory(entityFactory);
    }

    @Bean
    public DtoMapper dtoMapper(DependenciesResolver resolver) {
        return DtoMapper.defaultInstance(new ApplicationScanner(), resolver, getEnvironment());
    }

    @Override
    @Bean
    public EntityTriggerManager entityTriggerManager(DependenciesResolver dependenciesResolver) {
        return super.entityTriggerManager(dependenciesResolver);
    }
}
