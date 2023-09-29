package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.utils.DateTimeUtils;
import com.bachlinh.order.web.dto.resp.OrderOfCustomerResp;

@ActiveReflection
public class OrderOfCustomerResp$OrderInfoStrategy extends AbstractDtoStrategy<OrderOfCustomerResp.OrderInfo, Order> {

    private OrderOfCustomerResp$OrderInfoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(Order source, Class<OrderOfCustomerResp.OrderInfo> type) {
        // Do nothing
    }

    @Override
    protected OrderOfCustomerResp.OrderInfo doConvert(Order source, Class<OrderOfCustomerResp.OrderInfo> type) {
        OrderOfCustomerResp.OrderInfo result = new OrderOfCustomerResp.OrderInfo();
        result.setId(source.getId());
        result.setStatus(source.getOrderStatus().getStatus());
        result.setOrderTime(DateTimeUtils.convertOutputDateTime(source.getTimeOrder()));
        return result;
    }

    @Override
    protected void afterConvert(Order source, Class<OrderOfCustomerResp.OrderInfo> type) {
        // Do nothing
    }

    @Override
    protected DtoStrategy<OrderOfCustomerResp.OrderInfo, Order> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new OrderOfCustomerResp$OrderInfoStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<OrderOfCustomerResp.OrderInfo> getTargetType() {
        return OrderOfCustomerResp.OrderInfo.class;
    }
}
