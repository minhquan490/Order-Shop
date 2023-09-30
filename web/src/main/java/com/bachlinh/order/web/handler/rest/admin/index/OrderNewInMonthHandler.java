package com.bachlinh.order.web.handler.rest.admin.index;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.AnalyzeOrderNewInMonthResp;
import com.bachlinh.order.web.service.business.OrderAnalyzeService;

@ActiveReflection
@RouteProvider(name = "orderNewInMonthHandler")
public class OrderNewInMonthHandler extends AbstractController<AnalyzeOrderNewInMonthResp, Void> {
    private OrderAnalyzeService orderAnalyzeService;
    private String url;

    private OrderNewInMonthHandler() {
    }

    @Override
    public AbstractController<AnalyzeOrderNewInMonthResp, Void> newInstance() {
        return new OrderNewInMonthHandler();
    }

    @Override
    @ActiveReflection
    protected AnalyzeOrderNewInMonthResp internalHandler(Payload<Void> request) {
        return orderAnalyzeService.analyzeNewOrderInMonth();
    }

    @Override
    protected void inject() {
        if (orderAnalyzeService == null) {
            orderAnalyzeService = resolveService(OrderAnalyzeService.class);
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
