package com.bachlinh.order.security.filter.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.oauth2.jwt.JwtException;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerHistory;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.repository.CustomerHistoryRepository;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.RefreshTokenRepository;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.enums.RequestType;
import com.bachlinh.order.security.filter.GrpcWebFilter;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.HeaderUtils;
import com.bachlinh.order.utils.JacksonUtils;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class LoggingRequestFilter extends GrpcWebFilter {
    private static final Logger log = LogManager.getLogger(LoggingRequestFilter.class);
    private static final int REMOVAL_POLICY_YEAR = 1;
    private final ObjectMapper objectMapper = JacksonUtils.getSingleton();
    private CustomerHistoryRepository customerHistoryRepository;
    private CustomerRepository customerRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private ThreadPoolTaskExecutor executor;
    private TokenManager tokenManager;
    private EntityFactory entityFactory;

    public LoggingRequestFilter(DependenciesResolver dependenciesResolver, String profile) {
        super(dependenciesResolver, profile);
    }

    @Override
    public LoggingRequestFilter setExcludePaths(Collection<String> excludePaths) {
        return this;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return false;
    }

    @Override
    protected void intercept(HttpServletRequest request, HttpServletResponse response) throws IOException {
        executor.execute(() -> logCustomerRequest(request));
    }

    @Override
    protected void inject() {
        if (executor == null) {
            executor = getDependenciesResolver().resolveDependencies(ThreadPoolTaskExecutor.class);
        }
        if (customerHistoryRepository == null) {
            customerHistoryRepository = getDependenciesResolver().resolveDependencies(CustomerHistoryRepository.class);
        }
        if (tokenManager == null) {
            tokenManager = getDependenciesResolver().resolveDependencies(TokenManager.class);
        }
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
        if (customerRepository == null) {
            customerRepository = getDependenciesResolver().resolveDependencies(CustomerRepository.class);
        }
        if (refreshTokenRepository == null) {
            refreshTokenRepository = getDependenciesResolver().resolveDependencies(RefreshTokenRepository.class);
        }
    }

    private void logCustomerRequest(HttpServletRequest request) {
        logWithJwt(request);
    }

    private void logWithJwt(HttpServletRequest request) {
        String jwt = HeaderUtils.getAuthorizeHeader(request);
        try {
            Map<String, Object> claims = tokenManager.getClaimsFromToken(jwt);
            String customerId = (String) claims.get(Customer_.ID);
            logCustomer(customerId, request);
        } catch (JwtException | IOException | NullPointerException e) {
            String refreshToken = HeaderUtils.getRefreshHeader(request);
            if (refreshToken == null) {
                log.debug("Log request of address [{}] failure", request.getRemoteAddr());
                return;
            }
            RefreshToken token = refreshTokenRepository.getRefreshToken(refreshToken);
            if (token == null) {
                log.debug("Invalid user has address [{}] request to endpoint [{}]", request.getRemoteAddr(), request.getContextPath());
                return;
            }
            try {
                logCustomer(token.getCustomer().getId(), request);
            } catch (IOException e1) {
                log.debug("Log customer has id [{}] failure because IOException. Message [{}]", token.getCustomer().getId(), e1.getMessage());
            }
        }
    }

    private void logCustomer(String customerId, HttpServletRequest request) throws IOException {
        CustomerHistory customerHistory = entityFactory.getEntity(CustomerHistory.class);
        Customer customer = customerRepository.getCustomerById(customerId, true);
        if (customer.getRole().equalsIgnoreCase(Role.ADMIN.name())) {
            return;
        }
        customerHistory.setCustomer(customer);
        customerHistory.setPathRequest(request.getContextPath());
        customerHistory.setRequestType(determineRequest(request.getContextPath()).name());
        customerHistory.setRequestTime(Date.valueOf(LocalDate.now()));
        customerHistory.setRemoveTime(calculateDateRemoval());
        Map<?, ?> requestBody = objectMapper.readValue(request.getInputStream().readAllBytes(), Map.class);
        customerHistory.setRequestContent(objectMapper.writeValueAsString(requestBody));
        customerHistoryRepository.saveCustomerHistory(customerHistory);
    }

    private RequestType determineRequest(String request) {
        if (request.contains("search")) {
            return RequestType.SEARCH;
        } else if (request.contains("category")) {
            return RequestType.CATEGORY;
        } else if (request.contains("product")) {
            return RequestType.PRODUCT;
        } else {
            return RequestType.NONE;
        }
    }

    private Date calculateDateRemoval() {
        LocalDate now = LocalDate.now();
        int year = now.getYear() + REMOVAL_POLICY_YEAR;
        return Date.valueOf(LocalDate.of(year, now.getMonth(), now.getDayOfMonth()));
    }
}
