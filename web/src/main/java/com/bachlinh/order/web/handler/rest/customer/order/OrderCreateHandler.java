package com.bachlinh.order.web.handler.rest.customer.order;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.customer.OrderCreateForm;
import com.bachlinh.order.web.dto.resp.OrderResp;
import com.bachlinh.order.web.service.common.OrderService;

@RouteProvider(name = "orderCreateHandler")
@ActiveReflection
@Permit(roles = {Role.CUSTOMER, Role.ADMIN})
@EnableCsrf
public class OrderCreateHandler extends AbstractController<OrderResp, OrderCreateForm> {
    private String url;
    private OrderService orderService;

    private OrderCreateHandler() {
    }

    @Override
    public AbstractController<OrderResp, OrderCreateForm> newInstance() {
        return new OrderCreateHandler();
    }

    @Override
    @ActiveReflection
    protected OrderResp internalHandler(Payload<OrderCreateForm> request) {
        return orderService.saveOrder(request.data());
    }

    @Override
    protected void inject() {
        if (orderService == null) {
            orderService = resolveService(OrderService.class);
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
