package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerCreateForm;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.common.CustomerService;

@ActiveReflection
@RouteProvider(name = "customerCreateHandler")
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class CustomerCreateHandler extends AbstractController<CustomerResp, CustomerCreateForm> {
    private String url;
    private CustomerService customerService;

    private CustomerCreateHandler() {
    }

    @Override
    public AbstractController<CustomerResp, CustomerCreateForm> newInstance() {
        return new CustomerCreateHandler();
    }

    @Override
    @ActiveReflection
    protected CustomerResp internalHandler(Payload<CustomerCreateForm> request) {
        var data = request.data();
        return customerService.saveCustomer(data);
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
            url = getEnvironment().getProperty("shop.url.admin.customer.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
