package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.customer.CustomerDeleteForm;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.common.CustomerService;

@ActiveReflection
@RouteProvider(name = "customerDeleteHandler")
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class CustomerDeleteHandler extends AbstractController<CustomerResp, CustomerDeleteForm> {
    private String url;
    private CustomerService customerService;

    private CustomerDeleteHandler() {
        super();
    }

    @Override
    public AbstractController<CustomerResp, CustomerDeleteForm> newInstance() {
        return new CustomerDeleteHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected CustomerResp internalHandler(Payload<CustomerDeleteForm> request) {
        return customerService.deleteCustomer(request.data());
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
            url = getEnvironment().getProperty("shop.url.admin.customer.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
