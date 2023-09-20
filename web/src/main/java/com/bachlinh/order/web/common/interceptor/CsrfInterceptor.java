package com.bachlinh.order.web.common.interceptor;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.controller.Controller;
import com.bachlinh.order.handler.controller.ControllerContext;
import com.bachlinh.order.handler.controller.ControllerContextHolder;
import com.bachlinh.order.handler.interceptor.spi.AbstractInterceptor;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.security.auth.spi.TemporaryTokenGenerator;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@ActiveReflection
public class CsrfInterceptor extends AbstractInterceptor {
    private static final long SEVEN_DAY_TO_SECOND = 86400L * 7L;
    private static final String CSRF_HEADER_KEY = "shop.client.csrf.header.key";

    private final Map<String, CsrfTokenHolder> csrfTokenHolderMap = Collections.synchronizedMap(new TreeMap<>());

    private ControllerContextHolder controllerContextHolder;
    private MessageSettingRepository messageSettingRepository;
    private TemporaryTokenGenerator tokenGenerator;
    private Environment environment;
    private ThreadPoolManager threadPoolManager;

    private CsrfInterceptor() {
    }

    @Override
    public boolean preHandle(NativeRequest<?> request, NativeResponse<?> response) {
        boolean enableCsrf = enableCsrf(request);
        if (enableCsrf) {
            String csrfToken = request.getCsrfToken();
            boolean csrfValid = contains(csrfToken);
            if (csrfValid) {
                removeToken(csrfToken);
            }
            return csrfValid;
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(NativeRequest<?> request, NativeResponse<?> response) {
        boolean enableCsrf = enableCsrf(request);
        if (enableCsrf) {
            configToken(response);
        } else {
            String token = request.getCsrfToken();
            if (token == null) {
                configToken(response);
            } else {
                response.addHeader(environment.getProperty(CSRF_HEADER_KEY), token);
            }
        }
    }

    @Override
    public AbstractInterceptor getInstance() {
        return new CsrfInterceptor();
    }

    @Override
    public void init() {
        configDependencies();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }

    private void configToken(NativeResponse<?> response) {
        String csrfToken = tokenGenerator.generateTempToken();
        response.addHeader(environment.getProperty(CSRF_HEADER_KEY), csrfToken);
        store(csrfToken);
    }

    private void configDependencies() {
        if (this.controllerContextHolder == null) {
            this.controllerContextHolder = getResolver().resolveDependencies(ControllerContextHolder.class);
        }
        if (this.messageSettingRepository == null) {
            this.messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
        if (this.tokenGenerator == null) {
            this.tokenGenerator = getResolver().resolveDependencies(TemporaryTokenGenerator.class);
        }
        if (environment == null) {
            String activeProfileName = Environment.getMainEnvironmentName();
            environment = Environment.getInstance(activeProfileName);
        }
        if (threadPoolManager == null) {
            threadPoolManager = getResolver().resolveDependencies(ThreadPoolManager.class);
            threadPoolManager.schedule(clearUnUseTokenTask(), getTrigger());
        }
    }

    private CronTrigger getTrigger() {
        return new CronTrigger("0 0 0 * * ?");
    }

    private Runnable clearUnUseTokenTask() {
        return () -> {
            var iterator = csrfTokenHolderMap.entrySet().iterator();
            long removeTime = LocalDateTime.now().plusDays(7).toEpochSecond(ZoneOffset.UTC);
            while (iterator.hasNext()) {
                var ele = iterator.next();
                CsrfTokenHolder holder = ele.getValue();
                long createdTime = holder.timeCreated().toEpochSecond(ZoneOffset.UTC);
                if (removeTime - createdTime >= SEVEN_DAY_TO_SECOND) {
                    iterator.remove();
                }
            }
        };
    }

    private boolean enableCsrf(NativeRequest<?> request) {
        ControllerContext controllerContext = controllerContextHolder.getContext();
        String requestPath = request.getUrl();
        RequestMethod requestMethod = request.getRequestMethod();
        Controller<?, ?> controller = controllerContext.getController(requestPath, requestMethod);
        Class<?> controllerType = controller.getClass();
        return controllerType.isAnnotationPresent(EnableCsrf.class);
    }

    private void removeToken(String token) {
        csrfTokenHolderMap.remove(token);
    }

    private boolean contains(String token) {
        if (token == null) {
            return false;
        }
        return csrfTokenHolderMap.containsKey(token);
    }

    private void store(String token) {
        CsrfTokenHolder holder = new CsrfTokenHolder(token, LocalDateTime.now());
        csrfTokenHolderMap.put(token, holder);
    }


    private record CsrfTokenHolder(String token, LocalDateTime timeCreated) {

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof String)) return false;
            return token.equals(obj);
        }

        @Override
        public int hashCode() {
            return token.hashCode();
        }
    }
}
