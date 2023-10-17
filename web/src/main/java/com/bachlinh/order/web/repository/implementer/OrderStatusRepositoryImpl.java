package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.web.repository.spi.OrderStatusRepository;

@RepositoryComponent
@ActiveReflection
public class OrderStatusRepositoryImpl extends AbstractRepository<Integer, OrderStatus> implements OrderStatusRepository, RepositoryBase {

    @ActiveReflection
    @DependenciesInitialize
    public OrderStatusRepositoryImpl(DependenciesResolver resolver) {
        super(OrderStatus.class, resolver);
    }

    @Override
    public void deleteStatus(OrderStatus status) {
        delete(status);
    }

    @Override
    public OrderStatus getStatusOfOrder(Order order) {
        return null;
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new OrderStatusRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{OrderStatusRepository.class};
    }
}
