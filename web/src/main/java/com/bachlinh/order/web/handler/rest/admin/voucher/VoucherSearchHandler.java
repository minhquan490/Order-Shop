package com.bachlinh.order.web.handler.rest.admin.voucher;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherSearchForm;
import com.bachlinh.order.web.dto.resp.VoucherResp;
import com.bachlinh.order.web.service.business.VoucherSearchService;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "voucherSearchHandler")
@Permit(roles = Role.ADMIN)
public class VoucherSearchHandler extends AbstractController<Collection<VoucherResp>, VoucherSearchForm> {
    private String url;
    private VoucherSearchService voucherSearchService;

    private VoucherSearchHandler() {
    }

    @Override
    public AbstractController<Collection<VoucherResp>, VoucherSearchForm> newInstance() {
        return new VoucherSearchHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<VoucherResp> internalHandler(Payload<VoucherSearchForm> request) {
        return voucherSearchService.search(request.data());
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (voucherSearchService == null) {
            voucherSearchService = resolver.resolveDependencies(VoucherSearchService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.voucher.search");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
