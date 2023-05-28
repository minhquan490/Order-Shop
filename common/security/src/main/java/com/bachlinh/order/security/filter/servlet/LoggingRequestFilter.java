package com.bachlinh.order.security.filter.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.bachlinh.order.security.filter.AbstractWebFilter;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.utils.HeaderUtils;
import com.bachlinh.order.utils.JacksonUtils;

import java.io.IOException;
import java.sql.Date;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Map;

/**
 * Logging filter to log coming request.
 *
 * @author Hoang Minh Quan
 */
public class LoggingRequestFilter extends AbstractWebFilter {
    private static final String H3_HEADER = "Alt-Svc";
    private static final int REMOVAL_POLICY_YEAR = 1;
    private static final Logger log = LogManager.getLogger(LoggingRequestFilter.class);
    private final String clientUrl;
    private final int h3Port;
    private final ObjectMapper objectMapper = JacksonUtils.getSingleton();
    private CustomerHistoryRepository customerHistoryRepository;
    private CustomerRepository customerRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private ThreadPoolTaskExecutor executor;
    private TokenManager tokenManager;
    private EntityFactory entityFactory;

    public LoggingRequestFilter(DependenciesContainerResolver containerResolver, String clientUrl, int h3Port) {
        super(containerResolver.getDependenciesResolver());
        this.clientUrl = clientUrl;
        this.h3Port = h3Port;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO implement ddos protection
        addH3Header(response);
        logRequest(request);
        executor.execute(() -> logCustomerRequest(request));
        filterChain.doFilter(request, response);
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

    /**
     * Log request metadata into rolling file.
     */
    private void logRequest(HttpServletRequest request) {
        String userAgent = HeaderUtils.getRequestHeaderValue("User-Agent", request);
        String referer = HeaderUtils.getRequestHeaderValue("Referer", request);
        log.info("user-agent: {}\nuser-locale: {}\nuser-address: {}\nreferer: {}\nrequest-path: {}",
                userAgent,
                request.getLocale(),
                request.getRemoteAddr(),
                referer,
                request.getServletPath());
        if (referer != null && !referer.contains(clientUrl)) {
            log.info("Request not come to client");
        }
    }

    private void addH3Header(HttpServletResponse response) {
        String h3Header = response.getHeader(H3_HEADER);
        if (h3Header == null) {
            String headerPattern = "h3=\":{0}\"; ma=2592000";
            response.addHeader(H3_HEADER, MessageFormat.format(headerPattern, h3Port).replace(",", ""));
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
