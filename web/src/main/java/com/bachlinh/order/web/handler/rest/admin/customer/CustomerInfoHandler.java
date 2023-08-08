package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.CustomerInfoResp;
import com.bachlinh.order.web.service.common.CustomerService;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider(name = "customerInfoHandler")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
@Permit(roles = Role.ADMIN)
public class CustomerInfoHandler extends AbstractController<CustomerInfoResp, Void> {
    private String url;
    private CustomerService customerService;

    @Override
    @ActiveReflection
    protected CustomerInfoResp internalHandler(Payload<Void> request) {
        var id = getNativeRequest().getUrlQueryParam().getFirst("userId");
        return customerService.getCustomerInfo(id);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (customerService == null) {
            customerService = resolver.resolveDependencies(CustomerService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.customer.info");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
