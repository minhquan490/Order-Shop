package com.bachlinh.order.web.handler.rest.common.address;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.ProvinceSearchForm;
import com.bachlinh.order.web.dto.resp.ProvinceResp;
import com.bachlinh.order.web.service.business.ProvinceSearchService;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "provinceSearchHandler")
public class ProvinceSearchHandler extends AbstractController<Collection<ProvinceResp>, ProvinceSearchForm> {
    private ProvinceSearchService provinceSearchService;
    private String url;

    private ProvinceSearchHandler() {
    }

    @Override
    public AbstractController<Collection<ProvinceResp>, ProvinceSearchForm> newInstance() {
        return new ProvinceSearchHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<ProvinceResp> internalHandler(Payload<ProvinceSearchForm> request) {
        return provinceSearchService.search(request.data());
    }

    @Override
    protected void inject() {
        if (provinceSearchService == null) {
            provinceSearchService = resolveService(ProvinceSearchService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.province.search");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
