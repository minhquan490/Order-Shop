package com.bachlinh.order.web.configuration;

import com.bachlinh.order.core.server.netty.channel.security.FilterChainAdapter;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.security.auth.internal.TokenManagerProvider;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.filter.servlet.AuthenticationFilter;
import com.bachlinh.order.security.filter.servlet.LoggingRequestFilter;
import com.bachlinh.order.security.filter.servlet.PermissionFilter;
import com.bachlinh.order.security.handler.AccessDeniedHandler;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.utils.HeaderUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Configuration
public class SecurityConfiguration {
    private List<String> lazyExcludeUrls;

    @Bean
    PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    AuthenticationFilter authenticationFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) {
        return new AuthenticationFilter(containerResolver, getExcludeUrls(profile), pathMatcher);
    }

    @Bean
    LoggingRequestFilter loggingRequestFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) {
        return new LoggingRequestFilter(containerResolver, profile, getExcludeUrls(profile), pathMatcher);
    }

    @Bean
    PermissionFilter permissionFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) {
        return new PermissionFilter(containerResolver, profile, getExcludeUrls(profile), pathMatcher);
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    TokenManager tokenManager(ApplicationContext applicationContext, @Value("${active.profile}") String profile) {
        TokenManagerProvider provider = new TokenManagerProvider(ContainerWrapper.wrap(applicationContext), profile);
        return provider.getTokenManager();
    }

    @Bean
    UnAuthorizationHandler unAuthorizationHandler() {
        return new UnAuthorizationHandler();
    }

    @Bean
    DefaultSecurityFilterChain filterChain(HttpSecurity http, DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) throws Exception {
        Environment environment = Environment.getInstance(profile);
        String clientUrl = environment.getProperty("shop.url.client");
        String urlAdmin = environment.getProperty("shop.url.pattern.admin");
        String urlCustomer = environment.getProperty("shop.url.pattern.customer");
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource(clientUrl, environment)))
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
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(urlAdmin).hasAnyAuthority(Role.ADMIN.name(), Role.SEO.name(), Role.MARKETING.name());
                    registry.requestMatchers(urlCustomer).hasAnyAuthority(Role.ADMIN.name(), Role.CUSTOMER.name(), Role.SEO.name(), Role.MARKETING.name());
                    registry.requestMatchers(getExcludeUrls(profile).toArray(new String[0])).permitAll();
                })
                .requestCache(RequestCacheConfigurer::disable)
                .jee(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .addFilterAfter(loggingRequestFilter(containerResolver, profile, pathMatcher), CsrfFilter.class)
                .addFilterBefore(authenticationFilter(containerResolver, profile, pathMatcher), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(permissionFilter(containerResolver, profile, pathMatcher), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    FilterChainProxy filterChainProxy(SecurityFilterChain securityFilterChain) {
        return new FilterChainProxy(securityFilterChain);
    }

    @Bean
    FilterChainAdapter filterChainAdapter(DependenciesContainerResolver containerResolver) {
        return new FilterChainAdapter(containerResolver.getDependenciesResolver());
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler();
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
            lazyExcludeUrls.add(environment.getProperty("shop.url.websocket"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.revoke-token"));
            lazyExcludeUrls.add(environment.getProperty("shop.url.basic-info"));
        }
        return lazyExcludeUrls;
    }
}
