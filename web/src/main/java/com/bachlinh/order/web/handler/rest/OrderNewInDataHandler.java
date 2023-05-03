package com.bachlinh.order.web.handler.rest;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.service.business.OrderInDateService;

@RouteProvider
@ActiveReflection
public class OrderNewInDataHandler extends AbstractController<Integer, Object> {
    private String url;
    private OrderInDateService orderInDateService;

    @ActiveReflection
    public OrderNewInDataHandler() {
    }

    @Override
    protected Integer internalHandler(Payload<Object> request) {
        return orderInDateService.numberOrderInDate();
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (orderInDateService == null) {
            orderInDateService = resolver.resolveDependencies(OrderInDateService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.order.new-in-date");
        }
        return null;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
