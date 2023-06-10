package com.bachlinh.order.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.ui.ModelMap;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.utils.HeaderUtils;

import java.util.Map;

@Slf4j
public final class RequestMonitor implements WebRequestInterceptor {
    private final ThreadLocal<StopWatch> clock = new ThreadLocal<>();
    private final TokenManager tokenManager;

    public RequestMonitor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public void preHandle(@NonNull WebRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        clock.set(stopWatch);
        log.info("Begin handle request of customer [{}]", getUsername(request));
    }

    @Override
    public void postHandle(@NonNull WebRequest request, ModelMap model) {
        clock.get().stop();
        log.info("End handle request of customer [{}]", getUsername(request));
    }

    @Override
    public void afterCompletion(@NonNull WebRequest request, Exception ex) {
        clock.remove();
        log.info("Completion handle request for customer [{}]", getUsername(request));
    }

    private String getUsername(WebRequest request) {
        String accessTokenHeader = HeaderUtils.getAuthorizeHeader();
        String accessToken = request.getHeader(accessTokenHeader);
        Map<String, Object> claim = tokenManager.getClaimsFromToken(accessToken);
        return (String) claim.get(Customer_.USERNAME);
    }
}
