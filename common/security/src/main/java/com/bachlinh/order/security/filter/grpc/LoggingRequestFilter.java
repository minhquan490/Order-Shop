package com.bachlinh.order.security.filter.grpc;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.RefreshTokenRepository;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.enums.RequestType;
import com.bachlinh.order.security.filter.GrpcWebFilter;
import com.bachlinh.order.core.utils.HeaderUtils;
import com.bachlinh.order.core.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.oauth2.jwt.JwtException;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class LoggingRequestFilter extends GrpcWebFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int REMOVAL_POLICY_YEAR = 1;
    private final ObjectMapper objectMapper = JacksonUtils.getSingleton();
    private CustomerAccessHistoryRepository customerAccessHistoryRepository;
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
    protected void intercept(HttpServletRequest request, HttpServletResponse response) {
        executor.execute(() -> logCustomerRequest(request));
    }

    @Override
    protected void inject() {
        if (executor == null) {
            executor = resolveDependencies(ThreadPoolTaskExecutor.class);
        }
        if (customerAccessHistoryRepository == null) {
            customerAccessHistoryRepository = resolveRepository(CustomerAccessHistoryRepository.class);
        }
        if (tokenManager == null) {
            tokenManager = resolveDependencies(TokenManager.class);
        }
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
        }
        if (customerRepository == null) {
            customerRepository = resolveRepository(CustomerRepository.class);
        }
        if (refreshTokenRepository == null) {
            refreshTokenRepository = resolveRepository(RefreshTokenRepository.class);
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
                if (log.isDebugEnabled()) {
                    log.debug("Log request of address [{}] failure", request.getRemoteAddr());
                }
                return;
            }
            RefreshToken token = refreshTokenRepository.getRefreshToken(refreshToken);
            if (token == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid user has address [{}] request to endpoint [{}]", request.getRemoteAddr(), request.getContextPath());
                }
                return;
            }
            try {
                logCustomer(token.getCustomer().getId(), request);
            } catch (IOException e1) {
                log.warn("Log customer has id [{}] failure because IOException. Message [{}]", token.getCustomer().getId(), e1.getMessage());
            }
        }
    }

    private void logCustomer(String customerId, HttpServletRequest request) throws IOException {
        CustomerAccessHistory customerAccessHistory = entityFactory.getEntity(CustomerAccessHistory.class);
        Customer customer = customerRepository.getCustomerForAuthentication(customerId);
        if (customer.getRole().equalsIgnoreCase(Role.ADMIN.name())) {
            return;
        }
        customerAccessHistory.setCustomer(customer);
        customerAccessHistory.setPathRequest(request.getContextPath());
        customerAccessHistory.setRequestType(determineRequest(request.getContextPath()).name());
        customerAccessHistory.setRequestTime(Date.valueOf(LocalDate.now()));
        customerAccessHistory.setRemoveTime(calculateDateRemoval());
        Map<?, ?> requestBody = objectMapper.readValue(request.getInputStream().readAllBytes(), Map.class);
        customerAccessHistory.setRequestContent(objectMapper.writeValueAsString(requestBody));
        customerAccessHistoryRepository.saveCustomerHistory(customerAccessHistory);
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
