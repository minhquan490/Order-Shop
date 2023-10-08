package com.bachlinh.order.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.entity.trigger.AbstractTrigger;
import com.bachlinh.order.repository.OrderDetailRepository;
import com.bachlinh.order.repository.OrderHistoryRepository;
import com.bachlinh.order.repository.OrderStatusRepository;

import java.util.Collection;

@ApplyOn(entity = Order.class)
@ActiveReflection
public class OrderAssociateDeletionTrigger extends AbstractTrigger<Order> {

    private OrderStatusRepository orderStatusRepository;
    private OrderHistoryRepository orderHistoryRepository;
    private OrderDetailRepository orderDetailRepository;

    @Override
    public TriggerMode getMode() {
        return TriggerMode.BEFORE;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_DELETE};
    }

    @Override
    public String getTriggerName() {
        return "OrderAssociateDeletion";
    }

    @Override
    protected void doExecute(Order entity) {
        OrderStatus orderStatus = orderStatusRepository.getStatusOfOrder(entity);
        orderStatusRepository.deleteStatus(orderStatus);

        OrderHistory orderHistory = orderHistoryRepository.getHistoryOfOrder(entity);
        orderHistoryRepository.deleteOrderHistory(orderHistory);

        Collection<OrderDetail> orderDetails = orderDetailRepository.getOrderDetailsOfOrder(entity);
        orderDetailRepository.deleteOrderDetails(orderDetails);
    }

    @Override
    protected void inject() {
        if (orderStatusRepository == null) {
            orderStatusRepository = resolveRepository(OrderStatusRepository.class);
        }
        if (orderHistoryRepository == null) {
            orderHistoryRepository = resolveRepository(OrderHistoryRepository.class);
        }
        if (orderDetailRepository == null) {
            orderDetailRepository = resolveRepository(OrderDetailRepository.class);
        }
    }
}
