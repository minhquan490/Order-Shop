package com.bachlinh.order.security.filter.servlet;

import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.http.DelegateHttpServletRequest;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.RefreshTokenRepository;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.security.enums.RequestType;
import com.bachlinh.order.security.filter.AbstractWebFilter;
import com.bachlinh.order.security.helper.RequestAccessHistoriesHolder;
import com.bachlinh.order.utils.HeaderUtils;
import com.bachlinh.order.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

/**
 * Logging filter to log coming request.
 *
 * @author Hoang Minh Quan
 */
public class LoggingRequestFilter extends AbstractWebFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final int REMOVAL_POLICY_YEAR = 1;
    private final Environment environment;
    private final ObjectMapper objectMapper = JacksonUtils.getSingleton();
    private CustomerAccessHistoryRepository customerAccessHistoryRepository;
    private CustomerRepository customerRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private ThreadPoolManager threadPoolManager;
    private TokenManager tokenManager;
    private EntityFactory entityFactory;
    private final Collection<String> excludePaths;
    private final PathMatcher pathMatcher;// Use AntMatcher

    public LoggingRequestFilter(DependenciesContainerResolver containerResolver, String profile, Collection<String> excludePaths, PathMatcher pathMatcher) {
        super(containerResolver.getDependenciesResolver());
        this.environment = Environment.getInstance(profile);
        this.excludePaths = excludePaths;
        this.pathMatcher = pathMatcher;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return excludePaths.stream().anyMatch(path -> pathMatcher.match(path, request.getRequestURI()));
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isClientFetch(request)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        HttpServletRequest delegateHttpServletRequest = new DelegateHttpServletRequest(request);
        if (response.isCommitted()) {
            return;
        }
        threadPoolManager.execute(() -> logCustomerRequest(delegateHttpServletRequest), RunnableType.HTTP);
        filterChain.doFilter(delegateHttpServletRequest, response);
    }

    @Override
    protected void inject() {
        if (threadPoolManager == null) {
            threadPoolManager = resolveDependencies(ThreadPoolManager.class);
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
                try {
                    logAnonymousCustomer(request);
                } catch (IOException e1) {
                    log.warn("Log customer has ip [{}] failure because IOException. Message [{}]", request.getRemoteAddr(), e1.getMessage(), e1);
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
                log.warn("Log customer has id [{}] failure because IOException. Message [{}]", token.getCustomer().getId(), e1.getMessage(), e1);
            }
        }
    }

    private void logAnonymousCustomer(HttpServletRequest request) throws IOException {
        CustomerAccessHistory customerAccessHistory = createCommonHistory(request);
        saveLog(customerAccessHistory);
    }

    private void logCustomer(String customerId, HttpServletRequest request) throws IOException {
        CustomerAccessHistory customerAccessHistory = createCommonHistory(request);
        Customer customer = customerRepository.getCustomerForAuthentication(customerId);
        customerAccessHistory.setCustomer(customer);
        saveLog(customerAccessHistory);
    }

    private void saveLog(CustomerAccessHistory customerAccessHistory) {
        RequestAccessHistoriesHolder.saveHistories(customerAccessHistory, customerAccessHistoryRepository);
    }

    private CustomerAccessHistory createCommonHistory(HttpServletRequest request) throws IOException {
        CustomerAccessHistory customerAccessHistory = entityFactory.getEntity(CustomerAccessHistory.class);
        customerAccessHistory.setPathRequest(request.getRequestURI());
        customerAccessHistory.setRequestType(determineRequest(request.getRequestURI()).name());
        customerAccessHistory.setRequestTime(Date.valueOf(LocalDate.now()));
        customerAccessHistory.setRemoveTime(calculateDateRemoval());
        byte[] requestData = request.getInputStream().readAllBytes();
        if (requestData.length != 0) {
            Map<?, ?> requestBody = objectMapper.readValue(requestData, Map.class);
            customerAccessHistory.setRequestContent(objectMapper.writeValueAsString(requestBody));
        }
        customerAccessHistory.setCustomerIp(request.getRemoteAddr());
        return customerAccessHistory;
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

    private boolean isClientFetch(HttpServletRequest request) {
        String headerKey = environment.getProperty("shop.client.identify.header.key");
        String headerValue = environment.getProperty("shop.client.identify.header.value");
        String actualHeader = request.getHeader(headerKey);
        return actualHeader != null && actualHeader.equals(headerValue);
    }
}
