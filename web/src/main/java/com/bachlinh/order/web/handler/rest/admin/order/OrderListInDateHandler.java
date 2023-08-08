package com.bachlinh.order.web.handler.rest.admin.order;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.OrderListResp;
import com.bachlinh.order.web.service.business.OrderInDateService;
import lombok.NoArgsConstructor;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "orderListInDateHandler")
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Permit(roles = {Role.ADMIN, Role.MARKETING, Role.SEO})
public class OrderListInDateHandler extends AbstractController<Collection<OrderListResp>, Void> {
    private OrderInDateService orderInDateService;
    private String url;

    @Override
    @ActiveReflection
    protected Collection<OrderListResp> internalHandler(Payload<Void> request) {
        return orderInDateService.getOrdersInDate();
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (orderInDateService == null) {
            orderInDateService = resolver.resolveDependencies(OrderInDateService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.order.in-date");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
