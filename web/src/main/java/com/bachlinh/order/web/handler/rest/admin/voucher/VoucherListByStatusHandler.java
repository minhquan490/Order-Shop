package com.bachlinh.order.web.handler.rest.admin.voucher;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.VoucherResp;
import com.bachlinh.order.web.service.common.VoucherService;

import java.util.Collection;
import java.util.regex.Pattern;

@ActiveReflection
@RouteProvider(name = "voucherListByStatusHandler")
@Permit(roles = Role.ADMIN)
public class VoucherListByStatusHandler extends AbstractController<Collection<VoucherResp>, Void> {
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(?>true)$|^(?>false)$");
    private VoucherService voucherService;
    private String url;

    private VoucherListByStatusHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<VoucherResp>, Void> newInstance() {
        return new VoucherListByStatusHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<VoucherResp> internalHandler(Payload<Void> request) {
        String status = getNativeRequest().getUrlQueryParam().getFirst("status");
        if (!BOOLEAN_PATTERN.matcher(status).matches()) {
            throw new ResourceNotFoundException("Not found", getPath());
        }
        return voucherService.getVouchersByStatus(Boolean.parseBoolean(status));
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
            url = getEnvironment().getProperty("shop.url.admin.voucher.list.by-status");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
