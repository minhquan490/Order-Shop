package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerSearchForm;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.business.CustomerSearchingService;
import lombok.NoArgsConstructor;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "customerSearchHandler")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
@Permit(roles = Role.ADMIN)
public class CustomerSearchHandler extends AbstractController<Collection<CustomerResp>, CustomerSearchForm> {
    private String url;
    private CustomerSearchingService customerSearchingService;

    @Override
    @ActiveReflection
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
