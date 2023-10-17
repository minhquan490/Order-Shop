package com.bachlinh.order.web.configuration;

import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.utils.HeaderUtils;
import com.bachlinh.order.http.server.channel.security.FilterChainAdapter;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.web.common.security.AuthenticationFilter;
import com.bachlinh.order.web.common.security.LoggingRequestFilter;
import com.bachlinh.order.web.common.security.TokenManagerProvider;
import com.bachlinh.order.web.common.security.WrappedCorsFilter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

public abstract class SecurityModuleConfigure extends WebInterceptorConfigure {
    private List<String> lazyExcludeUrls;

    protected SecurityModuleConfigure(ApplicationScanner scanner) {
        super(scanner);
    }

    public PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }

    public AuthenticationFilter authenticationFilter(DependenciesContainerResolver containerResolver, String profile, PathMatcher pathMatcher) {
        return new AuthenticationFilter(containerResolver, getExcludeUrls(profile), pathMatcher);
    }

    public LoggingRequestFilter loggingRequestFilter(DependenciesContainerResolver containerResolver, String profile, PathMatcher pathMatcher) {
        return new LoggingRequestFilter(containerResolver, profile, getExcludeUrls(profile), pathMatcher);
    }

    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    public TokenManager tokenManager(ApplicationContext applicationContext, String profile) {
        TokenManagerProvider provider = new TokenManagerProvider(ContainerWrapper.wrap(applicationContext), profile);
        return provider.getTokenManager();
    }

    public UnAuthorizationHandler unAuthorizationHandler() {
        return new UnAuthorizationHandler();
    }

    public SecurityFilterChain filterChain(HttpSecurity http, DependenciesContainerResolver containerResolver, String profile, PathMatcher pathMatcher) throws Exception {
        Environment environment = Environment.getInstance(profile);
        String clientUrl = environment.getProperty("shop.url.client");
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(httpSecurityHeadersConfigurer -> {
                    httpSecurityHeadersConfigurer.cacheControl(HeadersConfigurer.CacheControlConfig::disable);
                    httpSecurityHeadersConfigurer.httpStrictTransportSecurity(hstsConfig -> {
                        hstsConfig.includeSubDomains(true);
                        hstsConfig.maxAgeInSeconds(63072000);
                        hstsConfig.preload(true);
                    });
                    httpSecurityHeadersConfigurer.xssProtection(xXssConfig -> xXssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED));
                    httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
                })
                .requiresChannel(channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresSecure())
                .authorizeHttpRequests(registry -> registry.anyRequest().permitAll())
                .requestCache(RequestCacheConfigurer::disable)
                .jee(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .addFilterBefore(new WrappedCorsFilter(containerResolver.getDependenciesResolver(), corsConfigurationSource(clientUrl, environment)), CsrfFilter.class)
                .addFilterAfter(loggingRequestFilter(containerResolver, profile, pathMatcher), CsrfFilter.class)
                .addFilterBefore(authenticationFilter(containerResolver, profile, pathMatcher), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    public FilterChainProxy filterChainProxy(SecurityFilterChain securityFilterChain) {
        return new FilterChainProxy(securityFilterChain);
    }

    public FilterChainAdapter filterChainAdapter(DependenciesContainerResolver containerResolver) {
        return new FilterChainAdapter(containerResolver.getDependenciesResolver());
    }

    private CorsConfigurationSource corsConfigurationSource(String clientUrl, Environment environment) {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.addAllowedOrigin(clientUrl);
        configuration.addExposedHeader(HeaderUtils.getAuthorizeHeader());
        configuration.addExposedHeader(HeaderUtils.getRefreshHeader());
        configuration.addExposedHeader(environment.getProperty("shop.client.csrf.header.key"));

        corsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return corsConfigurationSource;
    }

    private List<String> getExcludeUrls(String profile) {
        if (lazyExcludeUrls == null) {
            Environment environment = Environment.getInstance(profile);
            lazyExcludeUrls = new LinkedList<>();
            lazyExcludeUrls.add(environment.getProperty("shop.url.pattern.base"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.login"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.register"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.home"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.pattern.resource"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.customer.reset.sending-mail"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.customer.reset.password"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.revoke-token"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.basic-info"));
        }
        return lazyExcludeUrls;
    }
}
