package com.bachlinh.order.web.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.handler.interceptor.spi.AbstractInterceptor;
import com.bachlinh.order.security.auth.spi.TokenManager;
import com.bachlinh.order.utils.HeaderUtils;

import java.util.Map;

@ActiveReflection
public final class RequestMonitor extends AbstractInterceptor {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ThreadLocal<StopWatch> clock = new ThreadLocal<>();
    private final TokenManager tokenManager;

    @ActiveReflection
    public RequestMonitor() {
        super();
        this.tokenManager = getResolver().resolveDependencies(TokenManager.class);
    }

    @Override
    public boolean preHandle(NativeRequest<?> request, NativeResponse<?> response) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        clock.set(stopWatch);
        var username = getUsername(request);
        log.info("Begin handle request of customer [{}]", username);
        return true;
    }

    @Override
    public void postHandle(NativeRequest<?> request, NativeResponse<?> response) {
        clock.get().stop();
        var username = getUsername(request);
        log.info("End handle request of customer [{}]", username);
    }

    @Override
    public void onComplete(NativeRequest<?> request, NativeResponse<?> response) {
        clock.remove();
        var username = getUsername(request);
        log.info("Completion handle request for customer [{}]", username);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    private String getUsername(NativeRequest<?> request) {
        String accessTokenHeader = HeaderUtils.getAuthorizeHeader();
        String accessToken = request.getHeaders().getFirst(accessTokenHeader);
        Map<String, Object> claim = tokenManager.getClaimsFromToken(accessToken);
        return (String) claim.get(Customer_.USERNAME);
    }
}
