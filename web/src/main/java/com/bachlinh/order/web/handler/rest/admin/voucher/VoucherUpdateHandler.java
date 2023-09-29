package com.bachlinh.order.web.handler.rest.admin.voucher;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherUpdateForm;
import com.bachlinh.order.web.dto.resp.VoucherResp;
import com.bachlinh.order.web.service.common.VoucherService;

@ActiveReflection
@RouteProvider(name = "voucherUpdateHandler")
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class VoucherUpdateHandler extends AbstractController<VoucherResp, VoucherUpdateForm> {
    private String url;
    private VoucherService voucherService;

    private VoucherUpdateHandler() {
    }

    @Override
    public AbstractController<VoucherResp, VoucherUpdateForm> newInstance() {
        return new VoucherUpdateHandler();
    }

    @Override
    @ActiveReflection
    protected VoucherResp internalHandler(Payload<VoucherUpdateForm> request) {
        return voucherService.updateVoucher(request.data());
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
            url = getEnvironment().getProperty("shop.url.admin.voucher.update");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
