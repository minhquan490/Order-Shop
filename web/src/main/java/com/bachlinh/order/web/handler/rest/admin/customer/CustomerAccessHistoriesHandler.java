package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.CustomerAccessHistoriesResp;
import com.bachlinh.order.web.service.common.CustomerAccessHistoriesService;

@RouteProvider
@ActiveReflection
@Permit(roles = Role.ADMIN)
public class CustomerAccessHistoriesHandler extends AbstractController<CustomerAccessHistoriesResp, Void> {
    private String url;
    private CustomerAccessHistoriesService customerAccessHistoriesService;

    private CustomerAccessHistoriesHandler() {
    }

    @Override
    public AbstractController<CustomerAccessHistoriesResp, Void> newInstance() {
        return new CustomerAccessHistoriesHandler();
    }

    @Override
    protected CustomerAccessHistoriesResp internalHandler(Payload<Void> request) {
        return customerAccessHistoriesService.getAccessHistories(getNativeRequest());
    }

    @Override
    protected void inject() {
        if (customerAccessHistoriesService == null) {
            customerAccessHistoriesService = resolveService(CustomerAccessHistoriesService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.customer.access-histories");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
