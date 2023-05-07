package com.bachlinh.order.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.security.auth.internal.TokenManagerProvider;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.filter.AuthenticationFilter;
import com.bachlinh.order.security.filter.ClientSecretFilter;
import com.bachlinh.order.security.filter.LoggingRequestFilter;
import com.bachlinh.order.security.filter.PermissionFilter;
import com.bachlinh.order.security.handler.ClientSecretHandler;
import com.bachlinh.order.security.handler.UnAuthorizationHandler;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    AuthenticationFilter authenticationFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) {
        return new AuthenticationFilter(containerResolver, getExcludeUrls(profile), pathMatcher);
    }

    @Bean
    LoggingRequestFilter loggingRequestFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile) {
        Environment environment = Environment.getInstance(profile);
        return new LoggingRequestFilter(containerResolver, environment.getProperty("shop.url.client"), Integer.parseInt(environment.getProperty("server.port")));
    }

    @Bean
    ClientSecretFilter clientSecretFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) {
        return new ClientSecretFilter(containerResolver, getExcludeUrls(profile), pathMatcher, profile);
    }

    @Bean
    PermissionFilter permissionFilter(DependenciesContainerResolver containerResolver, @Value("${active.profile}") String profile, PathMatcher pathMatcher) {
        return new PermissionFilter(containerResolver, profile, getExcludeUrls(profile), pathMatcher);
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(20);
    }

    @Bean
    ClientSecretHandler clientSecretHandler() {
        return new ClientSecretHandler();
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
                .csrf(csrf -> {
                    csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                    csrf.ignoringRequestMatchers(getExcludeUrls(profile).toArray(new String[0]));
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource(clientUrl)))
                .anonymous()
                .disable()
                .formLogin()
                .disable()
                .logout()
                .disable()
                .httpBasic()
                .disable()
                .headers()
                .cacheControl()
                .disable()
                .and()
                .requiresChannel()
                .anyRequest()
                .requiresSecure()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(urlAdmin).hasAuthority(Role.ADMIN.name())
                .requestMatchers(urlCustomer).hasAnyAuthority(Role.ADMIN.name(), Role.CUSTOMER.name())
                .requestMatchers(getExcludeUrls(profile).toArray(new String[0])).permitAll()
                .and()
                .addFilterAfter(loggingRequestFilter(containerResolver, profile), CsrfFilter.class)
                .addFilterBefore(authenticationFilter(containerResolver, profile, pathMatcher), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(clientSecretFilter(containerResolver, profile, pathMatcher), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(permissionFilter(containerResolver, profile, pathMatcher), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource(String clientUrl) {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.addAllowedOrigin(clientUrl);

        corsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return corsConfigurationSource;
    }

    private List<String> getExcludeUrls(String profile) {
        Environment environment = Environment.getInstance(profile);
        List<String> excludeUrls = new ArrayList<>();
        excludeUrls.add(environment.getProperty("shop.url.pattern.base"));
        excludeUrls.add(environment.getProperty("shop.url.login"));
        excludeUrls.add(environment.getProperty("shop.url.register"));
        excludeUrls.add(environment.getProperty("shop.url.home"));
        excludeUrls.add(environment.getProperty("shop.url.pattern.resource"));
        return excludeUrls;
    }
}
