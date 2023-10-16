package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.CustomerInfoResp;
import com.bachlinh.order.web.service.common.CustomerService;

@ActiveReflection
@RouteProvider(name = "customerInfoHandler")
@Permit(roles = Role.ADMIN)
public class CustomerInfoHandler extends AbstractController<CustomerInfoResp, Void> {
    private String url;
    private CustomerService customerService;

    private CustomerInfoHandler() {
        super();
    }

    @Override
    public AbstractController<CustomerInfoResp, Void> newInstance() {
        return new CustomerInfoHandler();
    }

    @Override
    @ActiveReflection
    protected CustomerInfoResp internalHandler(Payload<Void> request) {
        var id = getNativeRequest().getUrlQueryParam().getFirst("userId");
        return customerService.getCustomerInfo(id);
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
            url = getEnvironment().getProperty("shop.url.admin.customer.info");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
