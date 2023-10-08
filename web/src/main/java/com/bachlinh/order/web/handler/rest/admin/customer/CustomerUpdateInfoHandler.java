package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerUpdateInfoForm;
import com.bachlinh.order.web.dto.resp.CustomerInfoResp;
import com.bachlinh.order.web.service.common.CustomerService;

@ActiveReflection
@RouteProvider
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class CustomerUpdateInfoHandler extends AbstractController<CustomerInfoResp, CustomerUpdateInfoForm> {

    private CustomerService customerService;
    private String url;

    private CustomerUpdateInfoHandler() {
    }

    @Override
    public AbstractController<CustomerInfoResp, CustomerUpdateInfoForm> newInstance() {
        return new CustomerUpdateInfoHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected CustomerInfoResp internalHandler(Payload<CustomerUpdateInfoForm> request) {
        return customerService.updateCustomerFromAdminScreen(request.data());
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
            url = getEnvironment().getProperty("shop.url.admin.customer.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
