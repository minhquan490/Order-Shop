package com.bachlinh.order.web.handler.rest.admin.order;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.OrderOfCustomerResp;
import com.bachlinh.order.web.service.common.OrderService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = Role.ADMIN)
public class OrderOfCustomerHandler extends AbstractController<OrderOfCustomerResp, Void> {

    private String url;
    private OrderService orderService;

    @Override
    public AbstractController<OrderOfCustomerResp, Void> newInstance() {
        return new OrderOfCustomerHandler();
    }

    @Override
    protected OrderOfCustomerResp internalHandler(Payload<Void> request) {
        return orderService.getOrdersOfCustomer(getNativeRequest());
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
            url = getEnvironment().getProperty("shop.url.admin.order.orders-of-customer");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
