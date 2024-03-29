package com.bachlinh.order.web.handler.rest.common.address;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.WardSearchForm;
import com.bachlinh.order.web.dto.resp.WardResp;
import com.bachlinh.order.web.service.business.WardSearchService;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "wardSearchHandler")
public class WardSearchHandler extends AbstractController<Collection<WardResp>, WardSearchForm> {
    private String url;
    private WardSearchService wardSearchService;

    private WardSearchHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<WardResp>, WardSearchForm> newInstance() {
        return new WardSearchHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<WardResp> internalHandler(Payload<WardSearchForm> request) {
        return wardSearchService.search(request.data());
    }

    @Override
    protected void inject() {
        if (wardSearchService == null) {
            wardSearchService = resolveService(WardSearchService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.ward.search");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
