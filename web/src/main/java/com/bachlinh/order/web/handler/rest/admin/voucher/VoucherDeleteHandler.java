package com.bachlinh.order.web.handler.rest.admin.voucher;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherDeleteForm;
import com.bachlinh.order.web.service.common.VoucherService;
import lombok.NoArgsConstructor;

import java.util.Map;

@ActiveReflection
@RouteProvider(name = "voucherDeleteHandler")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class VoucherDeleteHandler extends AbstractController<Map<String, Object>, VoucherDeleteForm> {
    private String url;
    private VoucherService voucherService;

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<VoucherDeleteForm> request) {
        return voucherService.deleteVoucher(request.data());
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (voucherService == null) {
            voucherService = resolver.resolveDependencies(VoucherService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.voucher.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
