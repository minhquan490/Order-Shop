package com.bachlinh.order.web.handler.rest.common.address;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.ProvinceResp;
import com.bachlinh.order.web.service.common.ProvinceService;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "provinceListHandler")
public class ProvinceListHandler extends AbstractController<Collection<ProvinceResp>, Void> {
    private String url;
    private ProvinceService provinceService;

    private ProvinceListHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<ProvinceResp>, Void> newInstance() {
        return new ProvinceListHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<ProvinceResp> internalHandler(Payload<Void> request) {
        return provinceService.getAllProvince();
    }

    @Override
    protected void inject() {
        if (provinceService == null) {
            provinceService = resolveService(ProvinceService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.province.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
