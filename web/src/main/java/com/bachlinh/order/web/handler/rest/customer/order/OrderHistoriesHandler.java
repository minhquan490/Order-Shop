package com.bachlinh.order.web.handler.rest.customer.order;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.OrderHistoryResp;
import com.bachlinh.order.web.service.common.OrderHistoryService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@RouteProvider(name = "orderHistoriesHandler")
@ActiveReflection
@Permit(roles = {Role.CUSTOMER, Role.ADMIN})
public class OrderHistoriesHandler extends AbstractController<Collection<OrderHistoryResp>, Void> {
    private String url;
    private OrderHistoryService orderHistoryService;

    private OrderHistoriesHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<OrderHistoryResp>, Void> newInstance() {
        return new OrderHistoriesHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<OrderHistoryResp> internalHandler(Payload<Void> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderHistoryService.getOrderHistoriesOfCustomer(customer);
    }

    @Override
    protected void inject() {
        if (orderHistoryService == null) {
            orderHistoryService = resolveService(OrderHistoryService.class);
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
