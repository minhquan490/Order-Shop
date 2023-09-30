package com.bachlinh.order.web.handler.rest.admin.voucher;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.VoucherResp;
import com.bachlinh.order.web.service.common.VoucherService;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "voucherListHandler")
@Permit(roles = Role.ADMIN)
public class VoucherListHandler extends AbstractController<Collection<VoucherResp>, Void> {
    private VoucherService voucherService;
    private String url;

    private VoucherListHandler() {
    }

    @Override
    public AbstractController<Collection<VoucherResp>, Void> newInstance() {
        return new VoucherListHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<VoucherResp> internalHandler(Payload<Void> request) {
        return voucherService.getVouchers();
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
            url = getEnvironment().getProperty("shop.url.admin.voucher.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
