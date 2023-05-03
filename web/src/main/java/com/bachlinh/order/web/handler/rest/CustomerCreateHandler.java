package com.bachlinh.order.web.handler.rest;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.Form;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.CrudCustomerForm;
import com.bachlinh.order.web.dto.resp.CustomerInformationResp;
import com.bachlinh.order.web.service.common.CustomerService;

@ActiveReflection
@RouteProvider
public class CustomerCreateHandler extends AbstractController<CustomerInformationResp, CrudCustomerForm> {
    private String url;
    private CustomerService customerService;

    @ActiveReflection
    public CustomerCreateHandler() {
    }

    @Override
    protected CustomerInformationResp internalHandler(Payload<CrudCustomerForm> request) {
        return customerService.save(Form.wrap(request.data())).get();
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
            url = getEnvironment().getProperty("shop.url.admin.customer.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return null;
    }
}
