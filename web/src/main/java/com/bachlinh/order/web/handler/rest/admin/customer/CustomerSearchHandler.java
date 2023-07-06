package com.bachlinh.order.web.handler.rest.admin.customer;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerSearchForm;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.business.CustomerSearchingService;

import java.util.Collection;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class CustomerSearchHandler extends AbstractController<Collection<CustomerResp>, CustomerSearchForm> {
    private String url;
    private CustomerSearchingService customerSearchingService;

    @Override
    protected Collection<CustomerResp> internalHandler(Payload<CustomerSearchForm> request) {
        return customerSearchingService.search(request.data().getQuery());
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (customerSearchingService == null) {
            customerSearchingService = resolver.resolveDependencies(CustomerSearchingService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.customer.search");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
