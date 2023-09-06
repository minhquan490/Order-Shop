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
import com.bachlinh.order.web.dto.form.admin.customer.CustomerDeleteForm;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.common.CustomerService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider(name = "customerDeleteHandler")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class CustomerDeleteHandler extends AbstractController<CustomerResp, CustomerDeleteForm> {
    private String url;
    private CustomerService customerService;

    @Override
    public AbstractController<CustomerResp, CustomerDeleteForm> newInstance() {
        return new CustomerDeleteHandler();
    }

    @Override
    @ActiveReflection
    protected CustomerResp internalHandler(Payload<CustomerDeleteForm> request) {
        return customerService.deleteCustomer(request.data());
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
            url = getEnvironment().getProperty("shop.url.admin.customer.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
