package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.CustomerAssignmentVouchersResp;
import com.bachlinh.order.web.service.common.VoucherService;

@RouteProvider
@ActiveReflection
@Permit(roles = Role.ADMIN)
public class CustomerAssignmentVouchers extends AbstractController<CustomerAssignmentVouchersResp, Void> {

    private String url;
    private VoucherService voucherService;

    private CustomerAssignmentVouchers() {
        super();
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
        if (voucherService == null) {
            voucherService = resolveService(VoucherService.class);
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
