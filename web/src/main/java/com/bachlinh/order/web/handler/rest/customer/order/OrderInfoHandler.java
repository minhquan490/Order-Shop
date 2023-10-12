package com.bachlinh.order.web.handler.rest.customer.order;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.OrderInfoResp;
import com.bachlinh.order.web.service.common.OrderService;
import org.springframework.util.StringUtils;

@ActiveReflection
@RouteProvider(name = "orderInfoHandler")
@Permit(roles = {Role.CUSTOMER, Role.ADMIN})
public class OrderInfoHandler extends AbstractController<OrderInfoResp, Void> {
    private OrderService orderService;
    private String url;

    private OrderInfoHandler() {
        super();
    }

    @Override
    public AbstractController<OrderInfoResp, Void> newInstance() {
        return new OrderInfoHandler();
    }

    @Override
    @ActiveReflection
    protected OrderInfoResp internalHandler(Payload<Void> request) {
        var orderId = getNativeRequest().getUrlQueryParam().getFirst("id");
        if (!StringUtils.hasText(orderId) || !orderService.orderIsExist(orderId)) {
            throw new ResourceNotFoundException("Not found", getPath());
        }
        return orderService.getOrderInfoById(orderId);
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
            url = getEnvironment().getProperty("shop.url.customer.order.info");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
