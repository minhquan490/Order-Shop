package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.CustomerAccessHistoriesResp;
import com.bachlinh.order.web.service.common.CustomerAccessHistoriesService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = Role.ADMIN)
public class CustomerAccessHistoriesHandler extends AbstractController<CustomerAccessHistoriesResp, Void> {
    private String url;
    private CustomerAccessHistoriesService customerAccessHistoriesService;

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
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (customerAccessHistoriesService == null) {
            customerAccessHistoriesService = resolver.resolveDependencies(CustomerAccessHistoriesService.class);
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
