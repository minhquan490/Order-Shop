package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.entity.transaction.spi.EntitySavePointManager;
import com.bachlinh.order.web.repository.spi.OrderHistoryRepository;

@ActiveReflection
@ApplyOn(entity = Order.class)
public class OrderHistoryTrigger extends AbstractRepositoryTrigger<Order> {
    private EntityFactory entityFactory;
    private OrderHistoryRepository orderHistoryRepository;

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
            this.entityFactory = resolveDependencies(EntityFactory.class);
        }
        if (orderHistoryRepository == null) {
            orderHistoryRepository = resolveRepository(OrderHistoryRepository.class);
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
