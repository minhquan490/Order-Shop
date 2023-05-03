package com.bachlinh.order.web.handler.rest;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.Form;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.OrderProductForm;
import com.bachlinh.order.web.dto.resp.OrderResp;
import com.bachlinh.order.web.service.common.OrderService;

@RouteProvider
@ActiveReflection
public class OrderCreateHandler extends AbstractController<OrderResp, OrderProductForm> {
    private String url;
    private OrderService orderService;

    @ActiveReflection
    public OrderCreateHandler() {
    }

    @Override
    protected OrderResp internalHandler(Payload<OrderProductForm> request) {
        return orderService.save(Form.wrap(request.data())).get();
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (orderService == null) {
            orderService = resolver.resolveDependencies(OrderService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.customer.order.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
