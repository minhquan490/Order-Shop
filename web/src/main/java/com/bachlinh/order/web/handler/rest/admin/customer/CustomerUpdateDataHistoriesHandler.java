package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.CustomerUpdateDataHistoriesResp;
import com.bachlinh.order.web.service.common.CustomerInfoChangeService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = Role.ADMIN)
public class CustomerUpdateDataHistoriesHandler extends AbstractController<CustomerUpdateDataHistoriesResp, Void> {

    private String url;
    private CustomerInfoChangeService customerInfoChangeService;

    @Override
    public AbstractController<CustomerUpdateDataHistoriesResp, Void> newInstance() {
        return new CustomerUpdateDataHistoriesHandler();
    }

    @Override
    protected CustomerUpdateDataHistoriesResp internalHandler(Payload<Void> request) {
        return customerInfoChangeService.getCustomerInfoChangeHistories(getNativeRequest());
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (customerInfoChangeService == null) {
            customerInfoChangeService = resolver.resolveDependencies(CustomerInfoChangeService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.customer.updated-data-histories");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
