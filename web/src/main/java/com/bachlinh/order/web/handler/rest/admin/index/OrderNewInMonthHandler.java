package com.bachlinh.order.web.handler.rest.admin.index;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.AnalyzeOrderNewInMonthResp;
import com.bachlinh.order.web.service.business.OrderAnalyzeService;
import lombok.NoArgsConstructor;

@ActiveReflection
@RouteProvider(name = "orderNewInMonthHandler")
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class OrderNewInMonthHandler extends AbstractController<AnalyzeOrderNewInMonthResp, Void> {
    private OrderAnalyzeService orderAnalyzeService;
    private String url;

    @Override
    @ActiveReflection
    protected AnalyzeOrderNewInMonthResp internalHandler(Payload<Void> request) {
        return orderAnalyzeService.analyzeNewOrderInMonth();
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (orderAnalyzeService == null) {
            orderAnalyzeService = resolver.resolveDependencies(OrderAnalyzeService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.order.order-in-month-analyze");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
