package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.OrderInfoResp;

@ActiveReflection
public class OrderInfoStrategy extends AbstractDtoStrategy<OrderInfoResp, Order> {

    @ActiveReflection
    public OrderInfoStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(Order source, Class<OrderInfoResp> type) {
        // Do nothing
    }

    @Override
    protected OrderInfoResp doConvert(Order source, Class<OrderInfoResp> type) {
        var resp = new OrderInfoResp();
        resp.setId(source.getId());
        resp.setDeposited(source.isDeposited());
        resp.setTimeOrder(source.getTimeOrder().toString());
        resp.setOrderStatus(source.getOrderStatus().getStatus());
        resp.setTransactionCode(source.getBankTransactionCode());
        int totalPrice = source.getOrderDetails()
                .stream()
                .mapToInt(orderDetail -> orderDetail.getAmount() * orderDetail.getProduct().getPrice())
                .sum();
        resp.setTotalPrice(totalPrice);
        var details = source.getOrderDetails()
                .stream()
                .map(orderDetail -> {
                    var detail = new OrderInfoResp.Details();
                    detail.setProductName(orderDetail.getProduct().getName());
                    detail.setAmount(orderDetail.getAmount());
                    detail.setProductPrice(orderDetail.getProduct().getPrice());
                    return detail;
                }).toList()
                .toArray(new OrderInfoResp.Details[0]);
        resp.setDetails(details);
        return resp;
    }

    @Override
    protected void afterConvert(Order source, Class<OrderInfoResp> type) {
        // Do nothing
    }

    @Override
    public Class<OrderInfoResp> getTargetType() {
        return OrderInfoResp.class;
    }
}
