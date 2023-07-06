package com.bachlinh.order.web.handler.rest.customer.order;

import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.OrderHistoryResp;
import com.bachlinh.order.web.service.common.OrderHistoryService;

import java.util.Collection;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class OrderHistoriesHandler extends AbstractController<Collection<OrderHistoryResp>, Void> {
    private String url;
    private OrderHistoryService orderHistoryService;

    @Override
    protected Collection<OrderHistoryResp> internalHandler(Payload<Void> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderHistoryService.getOrderHistoriesOfCustomer(customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (orderHistoryService == null) {
            orderHistoryService = resolver.resolveDependencies(OrderHistoryService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.customer.order-history.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
