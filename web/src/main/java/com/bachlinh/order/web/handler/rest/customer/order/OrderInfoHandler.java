package com.bachlinh.order.web.handler.rest.customer.order;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.OrderInfoResp;
import com.bachlinh.order.web.service.common.OrderService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@ActiveReflection
@RouteProvider(name = "orderInfoHandler")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Permit(roles = {Role.CUSTOMER, Role.ADMIN})
public class OrderInfoHandler extends AbstractController<OrderInfoResp, Void> {
    private OrderService orderService;
    private String url;

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
        var resolver = getContainerResolver().getDependenciesResolver();
        if (orderService == null) {
            orderService = resolver.resolveDependencies(OrderService.class);
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
