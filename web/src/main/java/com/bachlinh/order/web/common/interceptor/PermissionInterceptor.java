package com.bachlinh.order.web.common.interceptor;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.exception.http.AccessDeniedException;
import com.bachlinh.order.handler.controller.ControllerContextHolder;
import com.bachlinh.order.handler.interceptor.spi.AbstractInterceptor;
import com.bachlinh.order.repository.MessageSettingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.MessageFormat;
import java.util.Arrays;


@ActiveReflection
public class PermissionInterceptor extends AbstractInterceptor {
    private static final String NOT_PERMISSION_MESSAGE_ID = "MSG-000033";

    private ControllerContextHolder controllerContextHolder;
    private MessageSettingRepository messageSettingRepository;

    @ActiveReflection
    public PermissionInterceptor() {
        super();
    }

    @Override
    public boolean preHandle(NativeRequest<?> request, NativeResponse<?> response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var controllerClass = controllerContextHolder.getContext().getController(request.getUrl(), request.getRequestMethod()).getClass();
        if (!controllerClass.isAnnotationPresent(Permit.class)) {
            return super.preHandle(request, response);
        }
        if (authentication == null) {
            throw createAccessDeniedException(request);
        }
        Customer customer = (Customer) authentication.getPrincipal();
        Permit permit = controllerClass.getAnnotation(Permit.class);
        Role customerRole = Role.of(customer.getRole());
        boolean canAccess = Arrays.asList(permit.roles()).contains(customerRole);
        if (!canAccess) {
            throw createAccessDeniedException(request);
        }
        return super.preHandle(request, response);
    }

    @Override
    public void init() {
        configDependencies();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }

    private void configDependencies() {
        if (this.controllerContextHolder == null) {
            this.controllerContextHolder = getResolver().resolveDependencies(ControllerContextHolder.class);
        }
        if (this.messageSettingRepository == null) {
            this.messageSettingRepository = getResolver().resolveDependencies(MessageSettingRepository.class);
        }
    }

    private AccessDeniedException createAccessDeniedException(NativeRequest<?> request) {
        MessageSetting messageSetting = messageSettingRepository.getMessageById(NOT_PERMISSION_MESSAGE_ID);
        var exception = new AccessDeniedException(MessageFormat.format(messageSetting.getValue(), request.getUrl()), request.getUrl());
        exception.setIp(request.getCustomerIp());
        return exception;
    }
}
