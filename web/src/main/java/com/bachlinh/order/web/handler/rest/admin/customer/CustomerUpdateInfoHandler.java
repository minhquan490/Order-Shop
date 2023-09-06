package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerUpdateInfoForm;
import com.bachlinh.order.web.dto.resp.CustomerInfoResp;
import com.bachlinh.order.web.service.common.CustomerService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider
@Permit(roles = Role.ADMIN)
@EnableCsrf
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerUpdateInfoHandler extends AbstractController<CustomerInfoResp, CustomerUpdateInfoForm> {

    private CustomerService customerService;
    private String url;

    @Override
    public AbstractController<CustomerInfoResp, CustomerUpdateInfoForm> newInstance() {
        return new CustomerUpdateInfoHandler();
    }

    @Override
    protected CustomerInfoResp internalHandler(Payload<CustomerUpdateInfoForm> request) {
        return customerService.updateCustomerFromAdminScreen(request.data());
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (customerService == null) {
            customerService = resolver.resolveDependencies(CustomerService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.customer.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
