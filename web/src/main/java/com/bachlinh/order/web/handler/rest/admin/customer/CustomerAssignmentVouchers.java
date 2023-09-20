package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.CustomerAssignmentVouchersResp;
import com.bachlinh.order.web.service.common.VoucherService;

@RouteProvider
@ActiveReflection
@Permit(roles = Role.ADMIN)
public class CustomerAssignmentVouchers extends AbstractController<CustomerAssignmentVouchersResp, Void> {

    private String url;
    private VoucherService voucherService;

    private CustomerAssignmentVouchers() {
    }

    @Override
    public AbstractController<CustomerAssignmentVouchersResp, Void> newInstance() {
        return new CustomerAssignmentVouchers();
    }

    @Override
    protected CustomerAssignmentVouchersResp internalHandler(Payload<Void> request) {
        return voucherService.getAssignVouchers(getNativeRequest());
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (voucherService == null) {
            voucherService = resolver.resolveDependencies(VoucherService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.customer.assignment-vouchers");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
