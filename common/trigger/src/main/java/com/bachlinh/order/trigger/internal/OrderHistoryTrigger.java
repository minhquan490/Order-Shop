package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.entity.transaction.spi.EntitySavePointManager;
import com.bachlinh.order.repository.OrderHistoryRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

@ActiveReflection
@ApplyOn(entity = Order.class)
public class OrderHistoryTrigger extends AbstractTrigger<Order> {
    private EntityFactory entityFactory;
    private OrderHistoryRepository orderHistoryRepository;

    @ActiveReflection
    public OrderHistoryTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    @Override
    protected void doExecute(Order entity) {
        EntitySavePointManager savePointManager = entityFactory.getTransactionManager().getSavePointManager();
        savePointManager.createSavePoint("savepoint");
        OrderHistory orderHistory = entityFactory.getEntity(OrderHistory.class);
        orderHistory.setOrder(entity);
        orderHistory.setOrderTime(entity.getTimeOrder());
        orderHistory.setOrderStatus(entity.getOrderStatus().getStatus());
        try {
            orderHistoryRepository.saveOrderHistory(orderHistory);
        } catch (Exception e) {
            savePointManager.rollbackToSavePoint("savepoint");
        }
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            this.entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
        if (orderHistoryRepository == null) {
            orderHistoryRepository = getDependenciesResolver().resolveDependencies(OrderHistoryRepository.class);
        }
    }

    @Override
    public String getTriggerName() {
        return "orderHistoryTrigger";
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.BEFORE;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT};
    }
}
