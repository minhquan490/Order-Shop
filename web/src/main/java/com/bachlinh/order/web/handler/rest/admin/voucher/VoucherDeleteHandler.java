package com.bachlinh.order.web.handler.rest.admin.voucher;

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
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherDeleteForm;
import com.bachlinh.order.web.service.common.VoucherService;
import org.springframework.http.HttpStatus;

import java.util.Map;

@ActiveReflection
@RouteProvider(name = "voucherDeleteHandler")
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class VoucherDeleteHandler extends AbstractController<Map<String, Object>, VoucherDeleteForm> {
    private String url;
    private VoucherService voucherService;

    private VoucherDeleteHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, VoucherDeleteForm> newInstance() {
        return new VoucherDeleteHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<VoucherDeleteForm> request) {
        voucherService.deleteVoucher(request.data());
        return createDefaultResponse(HttpStatus.OK.value(), new String[]{"Delete successfully"});
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
            url = getEnvironment().getProperty("shop.url.admin.voucher.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
