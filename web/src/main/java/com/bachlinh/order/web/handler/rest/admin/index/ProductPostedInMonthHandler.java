package com.bachlinh.order.web.handler.rest.admin.index;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.AnalyzeProductPostedInMonthResp;
import com.bachlinh.order.web.service.business.ProductAnalyzeService;

@ActiveReflection
@RouteProvider(name = "productPostedInMonthHandler")
public class ProductPostedInMonthHandler extends AbstractController<AnalyzeProductPostedInMonthResp, Void> {
    private ProductAnalyzeService productAnalyzeService;
    private String url;

    private ProductPostedInMonthHandler() {
        super();
    }

    @Override
    public AbstractController<AnalyzeProductPostedInMonthResp, Void> newInstance() {
        return new ProductPostedInMonthHandler();
    }

    @Override
    @ActiveReflection
    protected AnalyzeProductPostedInMonthResp internalHandler(Payload<Void> request) {
        return productAnalyzeService.analyzeProductPostedInMonth();
    }

    @Override
    protected void inject() {
        if (productAnalyzeService == null) {
            productAnalyzeService = resolveService(ProductAnalyzeService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.product.product-in-month-analyze");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
