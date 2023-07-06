package com.bachlinh.order.web.handler.rest.common;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.DistrictSearchForm;
import com.bachlinh.order.web.dto.resp.DistrictResp;
import com.bachlinh.order.web.service.business.DistrictSearchService;

import java.util.Collection;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class DistrictSearchHandler extends AbstractController<Collection<DistrictResp>, DistrictSearchForm> {
    private String url;
    private DistrictSearchService districtSearchService;

    @Override
    @ActiveReflection
    protected Collection<DistrictResp> internalHandler(Payload<DistrictSearchForm> request) {
        return districtSearchService.search(request.data());
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (districtSearchService == null) {
            districtSearchService = resolver.resolveDependencies(DistrictSearchService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.district.search");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
