package com.bachlinh.order.web.handler.rest.common.address;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.ProvinceResp;
import com.bachlinh.order.web.service.common.ProvinceService;
import lombok.NoArgsConstructor;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "provinceListHandler")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class ProvinceListHandler extends AbstractController<Collection<ProvinceResp>, Void> {
    private String url;
    private ProvinceService provinceService;

    @Override
    @ActiveReflection
    protected Collection<ProvinceResp> internalHandler(Payload<Void> request) {
        return provinceService.getAllProvince();
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (provinceService == null) {
            provinceService = resolver.resolveDependencies(ProvinceService.class);
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
