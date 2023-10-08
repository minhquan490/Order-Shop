package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.CustomerLoginHistoryResp;
import com.bachlinh.order.web.service.common.LoginHistoryService;

@ActiveReflection
@Permit(roles = Role.ADMIN)
@RouteProvider
public class CustomerLoginHistoriesListHandler extends AbstractController<CustomerLoginHistoryResp, Void> {

    private String path;
    private LoginHistoryService loginHistoryService;

    private CustomerLoginHistoriesListHandler() {
    }

    @Override
    public AbstractController<CustomerLoginHistoryResp, Void> newInstance() {
        return new CustomerLoginHistoriesListHandler();
    }

    @Override
    protected CustomerLoginHistoryResp internalHandler(Payload<Void> request) {
        return loginHistoryService.getHistoriesOfCustomer(getNativeRequest(), getPath());
    }

    @Override
    protected void inject() {
        if (loginHistoryService == null) {
            loginHistoryService = resolveService(LoginHistoryService.class);
        }
    }

    @Override
    public String getPath() {
        if (path == null) {
            path = getEnvironment().getProperty("shop.url.admin.customer.login-histories");
        }
        return path;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
