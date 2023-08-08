package com.bachlinh.order.web.handler.rest.admin.index;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.AnalyzeProductPostedInMonthResp;
import com.bachlinh.order.web.service.business.ProductAnalyzeService;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider(name = "productPostedInMonthHandler")
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class ProductPostedInMonthHandler extends AbstractController<AnalyzeProductPostedInMonthResp, Void> {
    private ProductAnalyzeService productAnalyzeService;
    private String url;

    @Override
    @ActiveReflection
    protected AnalyzeProductPostedInMonthResp internalHandler(Payload<Void> request) {
        return productAnalyzeService.analyzeProductPostedInMonth();
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (productAnalyzeService == null) {
            productAnalyzeService = resolver.resolveDependencies(ProductAnalyzeService.class);
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
