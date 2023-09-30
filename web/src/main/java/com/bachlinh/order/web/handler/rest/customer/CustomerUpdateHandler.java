package com.bachlinh.order.web.handler.rest.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.customer.CustomerUpdateForm;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.common.CustomerService;

@RouteProvider(name = "customerUpdateHandler")
@ActiveReflection
@Permit(roles = Role.CUSTOMER)
@EnableCsrf
public class CustomerUpdateHandler extends AbstractController<CustomerResp, CustomerUpdateForm> {
    private String url;
    private CustomerService customerService;

    private CustomerUpdateHandler() {
    }

    @Override
    public AbstractController<CustomerResp, CustomerUpdateForm> newInstance() {
        return new CustomerUpdateHandler();
    }

    @Override
    @ActiveReflection
    protected CustomerResp internalHandler(Payload<CustomerUpdateForm> request) {
        return customerService.updateCustomer(request.data());
    }

    @Override
    protected void inject() {
        if (customerService == null) {
            customerService = resolveService(CustomerService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.customer.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
