package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.ValidationFailureException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.validate.rule.RuleManager;
import com.bachlinh.order.web.dto.form.admin.CustomerCreateForm;
import com.bachlinh.order.web.dto.resp.CustomerInformationResp;
import com.bachlinh.order.web.service.common.CustomerService;

@ActiveReflection
@RouteProvider
public class CustomerCreateHandler extends AbstractController<CustomerInformationResp, CustomerCreateForm> {
    private String url;
    private CustomerService customerService;
    private RuleManager ruleManager;

    @ActiveReflection
    public CustomerCreateHandler() {
    }

    @Override
    protected CustomerInformationResp internalHandler(Payload<CustomerCreateForm> request) {
        var data = request.data();
        var result = ruleManager.validate(data);
        if (!result.shouldHandle()) {
            throw new ValidationFailureException(result.getErrorResult(), getPath());
        }
        return customerService.saveCustomer(data);
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (customerService == null) {
            customerService = resolver.resolveDependencies(CustomerService.class);
        }
        if (ruleManager == null) {
            ruleManager = resolver.resolveDependencies(RuleManager.class);
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
